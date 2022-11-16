package com.seitov.messenger.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Membership;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Channel.AccessType;
import com.seitov.messenger.entity.Membership.Role;
import com.seitov.messenger.exception.NotAuthorizedException;
import com.seitov.messenger.exception.ResourceAlreadyExistsException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.MembershipRepository;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTests {
    
    @Mock
    MembershipRepository membershipRepository;

    @InjectMocks
    MembershipService membershipService;

    @Test
    public void create() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        Membership membership = new Membership(user, channel, Role.founder);
        when(membershipRepository.save(any(Membership.class))).thenAnswer(i -> i.getArguments()[0]);
        Membership created = membershipService.create(channel, user, Role.founder);
        assertEquals(membership.getUser(), created.getUser());
        assertEquals(membership.getChannel(), created.getChannel());
        assertEquals(membership.getRole(), created.getRole());
    }

    @Test
    public void createDuplicate() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        when(membershipRepository.save(any(Membership.class))).thenThrow(DataIntegrityViolationException.class);
        Exception ex = assertThrows(ResourceAlreadyExistsException.class, () -> membershipService.create(channel, user, Role.user));
        assertEquals("User already exist on this server!", ex.getMessage());
    }

    @Test
    public void delete() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        when(membershipRepository.deleteByUserAndChannel(user, channel)).thenReturn(1L);
        assertDoesNotThrow(() -> membershipService.delete(channel, user));
    }

    @Test
    public void deleteNonExisting() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        when(membershipRepository.deleteByUserAndChannel(user, channel)).thenReturn(0L);
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> membershipService.delete(channel, user));
        assertEquals("Member not found on this channel!", ex.getMessage());
    }

    @Test
    public void get() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        Membership membership = new Membership(user, channel, Role.user);
        when(membershipRepository.findByUserAndChannel(user, channel)).thenReturn(Optional.ofNullable(membership));
        assertEquals(membership, membershipService.get(channel, user));
    }

    @Test
    public void getNonExisting() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        when(membershipRepository.findByUserAndChannel(user, channel)).thenReturn(Optional.ofNullable(null));
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> membershipService.get(channel, user));
        assertEquals("Member not found on this channel!", ex.getMessage());
    }

    @Test
    public void changeMemberRole() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        Membership membership = new Membership(user, channel, Role.moderator);
        when(membershipRepository.findByUserAndChannel(user, channel)).thenReturn(Optional.ofNullable(membership));
        when((membershipRepository.save(any(Membership.class)))).thenAnswer(i -> i.getArguments()[0]);
        assertEquals(Role.admin, membershipService.changeMemberRole(channel, user, Role.admin).getRole());
    }

    @Test
    public void getPermissions() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        Membership membership = new Membership(user, channel, Role.user);
        when(membershipRepository.findByUserAndChannel(user, channel)).thenReturn(Optional.ofNullable(membership));
        assertEquals(0, membershipService.getPermissions(channel, user));
        membership.setRole(Role.moderator);
        assertEquals(1, membershipService.getPermissions(channel, user));
        membership.setRole(Role.admin);
        assertEquals(2, membershipService.getPermissions(channel, user));
        membership.setRole(Role.founder);
        assertEquals(3, membershipService.getPermissions(channel, user));
    }

    @Test
    public void getPermissionsNonMember() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        when(membershipRepository.findByUserAndChannel(any(User.class), any(Channel.class))).thenReturn(Optional.ofNullable(null));
        assertEquals(-1, membershipService.getPermissions(channel, user));
    }

    @Test
    public void checkPermissions() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        Membership membership = new Membership(user, channel, Role.user);
        when(membershipRepository.findByUserAndChannel(user, channel)).thenReturn(Optional.ofNullable(membership));
        assertEquals(0, membershipService.checkPermissions(channel, user, 0));
        membership.setRole(Role.moderator);
        assertEquals(1, membershipService.checkPermissions(channel, user, 1));
        membership.setRole(Role.admin);
        assertEquals(2, membershipService.checkPermissions(channel, user, 2));
        membership.setRole(Role.founder);
        assertEquals(3, membershipService.checkPermissions(channel, user, 3));    
    }

    @Test
    public void checkPermissionsNonMember() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        when(membershipRepository.findByUserAndChannel(user, channel)).thenReturn(Optional.ofNullable(null));
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> membershipService.checkPermissions(channel, user, 0));
        assertEquals("User not found on this channel!", ex.getMessage());
    }

    @Test
    public void checkPermissionsNotAuthorized() {
        Channel channel = new Channel(UUID.randomUUID(), "channel", null, null, AccessType.open);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        Membership membership = new Membership(user, channel, Role.user);
        when(membershipRepository.findByUserAndChannel(user, channel)).thenReturn(Optional.ofNullable(membership));
        Exception ex = assertThrows(NotAuthorizedException.class, () -> membershipService.checkPermissions(channel, user, 2));
        assertEquals("User dont have enough permissions on this channel!", ex.getMessage());
    }

}
