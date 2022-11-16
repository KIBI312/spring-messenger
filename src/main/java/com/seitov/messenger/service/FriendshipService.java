package com.seitov.messenger.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.seitov.messenger.entity.Friendship;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Friendship.Status;
import com.seitov.messenger.exception.IllegalDataException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.FriendshipRepository;

@Service
public class FriendshipService {
    
    private  FriendshipRepository friendshipRepository;
    private  UserService userService;

    @Autowired
    public FriendshipService(FriendshipRepository friendshipRepository, UserService userService) {
        this.userService = userService;
        this.friendshipRepository = friendshipRepository;
    }

    public Friendship sendFriendshipRequest(String user, String friend) throws IllegalDataException {
        try {
            Map<String, User> pair = userService.getUserPair(user, friend);
            Friendship friendship = new Friendship();
            friendship.setUser(pair.get(user));
            friendship.setFriend(pair.get(friend));
            friendship.setStatus(Status.pending);
            friendshipRepository.save(friendship);   
            return friendship;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalDataException("Request to this user already sent!");
        }
    }

    public Friendship acceptFriendshipRequest(String user, String friend) throws ResourceNotFoundException, IllegalDataException {
        Map<String, User> pair = userService.getUserPair(user, friend);
        Optional<Friendship> friendshipRequest = friendshipRepository.findByUserAndFriend(pair.get(user), pair.get(friend));
        if(friendshipRequest.isEmpty()) {
            throw new ResourceNotFoundException("Friend request not found!");
        }
        if(friendshipRequest.get().getStatus()!=Status.pending){
            throw new IllegalDataException("This request is already active or blocked!");
        }
        Optional<Friendship> reverseDirection = friendshipRepository.findByUserAndFriend(pair.get(friend), pair.get(user));
        Friendship friendship;
        if(reverseDirection.isEmpty()){
            friendship = new Friendship();
            friendship.setUser(pair.get(friend));
            friendship.setFriend(pair.get(user));
        } else {
            friendship = reverseDirection.get();
        }
        friendshipRequest.get().setStatus(Status.active);
        friendship.setStatus(Status.active);
        friendshipRepository.save(friendship);
        return friendshipRepository.save(friendshipRequest.get());
    }

    public Set<User> getFriends(String username) {
        User user = userService.getUser(username);
        Set<User> friends = new HashSet<>();
        friendshipRepository.findByUserAndStatus(user, Status.active).forEach(e -> {
            friends.add(e.getFriend());
        });
        return Collections.unmodifiableSet(friends);
    }

    public Set<Friendship> getFriendshipRequests(String username) {
        User user = userService.getUser(username);
        Set<Friendship> friendshipRequests = friendshipRepository.findByFriendAndStatus(user, Status.pending);
        return Collections.unmodifiableSet(friendshipRequests);
    }

    public Set<Friendship> getSentFriendshipRequests(String username) {
        User user = userService.getUser(username);
        Set<Friendship> friendshipRequests = friendshipRepository.findByUserAndStatus(user, Status.pending);
        return Collections.unmodifiableSet(friendshipRequests);
    }

}
