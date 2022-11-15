package com.seitov.messenger.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seitov.messenger.entity.Membership;
import com.seitov.messenger.dto.UserDto;
import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Membership.Role;
import com.seitov.messenger.exception.NotAuthorizedException;
import com.seitov.messenger.exception.ResourceAlreadyExistsException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.MembershipRepository;

@Service
public class MembershipService {

    private  MembershipRepository membershipRepository;

    @Autowired
    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public Membership create(Channel channel, User user, Role role) throws ResourceAlreadyExistsException {
        try {
            Membership membership = new Membership();
            membership.setChannel(channel);
            membership.setUser(user);
            membership.setRole(role);
            membershipRepository.save(membership);
            return membership;
        } catch (DataIntegrityViolationException e){
            throw new ResourceAlreadyExistsException("User already exist on this server!", e.getCause());
        }
    }

    @Transactional
    public void delete(Channel channel, User user) throws ResourceNotFoundException {
        long res = membershipRepository.deleteByUserAndChannel(user, channel);
        if(res==0){
            throw new ResourceNotFoundException("Member not found on this channel!");
        }
    }

    @Transactional
    public void deleteAllByChannel(Channel channel) {
        membershipRepository.deleteByChannel(channel);
    }

    public Membership get(Channel channel, User user) throws ResourceNotFoundException {
        Optional<Membership> membership = membershipRepository.findByUserAndChannel(user, channel);
        if(membership.isEmpty()) {
            throw new ResourceNotFoundException("Member not found on this channel!");
        }
        return membership.get();
    }

    public Membership update(Membership membership) {
        return membershipRepository.save(membership);
    }

    public Membership changeMemberRole(Channel channel, User user, Role role) throws ResourceNotFoundException {
        Optional<Membership> membership = membershipRepository.findByUserAndChannel(user, channel);
        if(membership.isEmpty()){
            throw new ResourceNotFoundException("User not found on this channel!");
        }
        membership.get().setRole(role);
        return membershipRepository.save(membership.get());
    }

    public Set<Channel> getMemberChannels(User user) {
        Set<Channel> channels = new HashSet<>();
        membershipRepository.findByUser(user).forEach(membership -> {
            channels.add(membership.getChannel());
        });
        return Collections.unmodifiableSet(channels);
    }

    public Set<UserDto> getChannelMembers(Channel channel) {
        Set<UserDto> members = new HashSet<>();
        membershipRepository.findByChannel(channel).forEach(e -> {
            members.add(new UserDto(e.getUser().getUsername(),e.getUser().getProfilePic()));
        });
        return Collections.unmodifiableSet(members);
    }

    public Set<UserDto> getChannelMembers(Channel channel, Role role) {
        Set<UserDto> members = new HashSet<>();
        membershipRepository.findByChannelAndRole(channel, role).forEach(e -> {
            members.add(new UserDto(e.getUser().getUsername(),e.getUser().getProfilePic()));
        });
        return Collections.unmodifiableSet(members);
    }

    public int getPermissions(Channel channel, User user) {
        Optional<Membership> membership = membershipRepository.findByUserAndChannel(user, channel);
        if(membership.isEmpty()){
            return -1;
        }
        return membership.get().getRole().getValue();
    }

    public int checkPermissions(Channel channel, User user, int permissions) throws ResourceNotFoundException, NotAuthorizedException {
        Optional<Membership> membership = membershipRepository.findByUserAndChannel(user, channel);
        if(membership.isEmpty()){
            throw new ResourceNotFoundException("User not found on this channel!");
        }
        int userPermissions = membership.get().getRole().getValue();
        if(userPermissions<permissions) {
            throw new NotAuthorizedException("User dont have enough permissions on this channel!");
        }
        return userPermissions;
    }

}
