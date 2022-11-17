package com.seitov.messenger.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.seitov.messenger.entity.Friendship;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Friendship.Status;
import com.seitov.messenger.exception.IllegalDataException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.FriendshipRepository;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTests {

    @Mock
    FriendshipRepository friendshipRepository;
    @Mock
    UserService userService;

    @InjectMocks
    FriendshipService friendshipService;

    @Test
    public void sendFriendshipRequest() {
        User user = new User();
        User friend = new User();
        mockUserServiceGetPair(user, friend);
        Friendship friendship = friendshipService.sendFriendshipRequest("user", "friend");
        assertEquals(user, friendship.getUser());
        assertEquals(friend, friendship.getFriend());
        assertEquals(Status.pending, friendship.getStatus());
    }

    @Test
    public void sendFriendshipRequestTwice() {
        User user = new User();
        User friend = new User();
        mockUserServiceGetPair(user, friend);
        Friendship friendship = new Friendship(null, user, friend, Status.pending);
        when(friendshipRepository.saveAndFlush(friendship)).thenThrow(DataIntegrityViolationException.class);
        when(userService.getUserPair("user", "friend"))
                .thenReturn(Map.of(user.getUsername(), user, friend.getUsername(), friend));
        Exception ex = assertThrows(IllegalDataException.class,
                () -> friendshipService.sendFriendshipRequest("user", "friend"));
        assertEquals("Request to this user already sent!", ex.getMessage());
    }

    @Test
    public void acceptNonExistingFriendRequest() {
        User user = new User();
        User friend = new User();
        mockUserServiceGetPair(user, friend);
        when(friendshipRepository.findByUserAndFriend(user, friend)).thenReturn(Optional.ofNullable(null));
        Exception ex = assertThrows(ResourceNotFoundException.class,
                () -> friendshipService.acceptFriendshipRequest(user.getUsername(), friend.getUsername()));
        assertEquals("Friend request not found!", ex.getMessage());
    }

    @Test
    public void acceptNonPendingFriendRequest() {
        User user = new User();
        User friend = new User();
        mockUserServiceGetPair(user, friend);
        Friendship friendship = new Friendship(null, user, friend, Status.active);
        when(friendshipRepository.findByUserAndFriend(user, friend)).thenReturn(Optional.ofNullable(friendship));
        Exception ex = assertThrows(IllegalDataException.class,
                () -> friendshipService.acceptFriendshipRequest(user.getUsername(), friend.getUsername()));
        assertEquals("This request is already active or blocked!", ex.getMessage());
    }

    @Test
    public void acceptFriendRequest() {
        User user = new User();
        User friend = new User();
        Friendship friendship = new Friendship(null, user, friend, Status.pending);
        mockUserServiceGetPair(user, friend);
        when(friendshipRepository.findByUserAndFriend(user, friend)).thenReturn(Optional.ofNullable(friendship));
        when(friendshipRepository.findByUserAndFriend(friend, user)).thenReturn(Optional.ofNullable(null));
        when(friendshipRepository.save(any(Friendship.class))).thenAnswer(i -> i.getArguments()[0]);
        Friendship accepted = friendshipService.acceptFriendshipRequest(user.getUsername(), friend.getUsername());
        assertEquals(Status.active, accepted.getStatus());
    }

    @Test
    public void getFriends() {
        Set<Friendship> friendships = new HashSet<>();
        Set<User> friends = new HashSet<>();
        User user = new User();
        mockUserServiceGetUser(user);
        for (int i = 0; i < 10; i++) {
            User friend = new User(UUID.randomUUID(), "friend"+i, null, null, null);
            friends.add(friend);
            friendships.add(new Friendship(null, user, friend, Status.active));
        }
        when(friendshipRepository.findByUserAndStatus(user, Status.active)).thenReturn(friendships);
        assertEquals(friends, friendshipService.getFriends(user.getUsername()));
    }

    private void mockUserServiceGetUser(User user) {
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("password");
        when(userService.getUser(user.getUsername())).thenReturn(user);
    }

    private void mockUserServiceGetPair(User user, User friend) {
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("password");
        friend.setId(UUID.randomUUID());
        friend.setUsername("friend");
        friend.setPassword("password");
        when(userService.getUserPair(user.getUsername(), friend.getUsername()))
                .thenReturn(Map.of(user.getUsername(), user, friend.getUsername(), friend));
    }

}
