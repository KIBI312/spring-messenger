package com.seitov.messenger.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.seitov.messenger.entity.Article;
import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Membership;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Channel.AccessType;
import com.seitov.messenger.entity.Membership.Role;
import com.seitov.messenger.exception.NotAuthorizedException;
import com.seitov.messenger.repository.ArticleRepository;

@ExtendWith(MockitoExtension.class)
public class ChannelManagementServiceTests {
    
    @Mock
    ChannelService channelService;
    @Mock
    MembershipService membershipService;
    @Mock
    UserService userService;
    @Mock
    ArticleRepository articleRepository;
    @Mock
    RoomService roomService;

    @InjectMocks
    ChannelManagementService channelManagementService;

    private Channel openChannel = new Channel(UUID.randomUUID(), "openChannel", null, null, AccessType.open);
    private User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");

    @Test
    public void createChannel() {
        Membership membership = new Membership(user, openChannel, Role.founder);
        when(membershipService.create(openChannel, user, Role.founder)).thenReturn(membership);
        assertEquals(openChannel, channelManagementService.createChannel(openChannel, user));
    }

    @Test
    public void addUserToChannel() {
        User admin = new User(UUID.randomUUID(), "admin", "pass", null, "USER");
        Membership membership = new Membership(user, openChannel, Role.user);
        when(membershipService.checkPermissions(openChannel, admin, 1)).thenReturn(2);
        when(userService.getUser(user.getUsername())).thenReturn(user);
        when(membershipService.create(openChannel, user, Role.user)).thenReturn(membership);
        assertEquals(membership, channelManagementService.addUserToChannel(openChannel, admin, user.getUsername()));
    }

    @Test 
    public void removeUserFromChannel() {
        User admin = new User(UUID.randomUUID(), "admin", "pass", null, "USER");
        when(membershipService.getPermissions(openChannel, admin)).thenReturn(2);
        when(userService.getUser(user.getUsername())).thenReturn(user);
        when(membershipService.getPermissions(openChannel, user)).thenReturn(1);
        assertDoesNotThrow(() -> channelManagementService.removeUserFromChannel(openChannel, admin, user.getUsername()));
    }

    @Test 
    public void removeUserFromChannelSameRoleAsUser() {
        User admin = new User(UUID.randomUUID(), "admin", "pass", null, "USER");
        when(membershipService.getPermissions(openChannel, admin)).thenReturn(2);
        when(userService.getUser(user.getUsername())).thenReturn(user);
        when(membershipService.getPermissions(openChannel, user)).thenReturn(2);
        Exception ex = assertThrows(NotAuthorizedException.class, () -> channelManagementService.removeUserFromChannel(openChannel, admin, user.getUsername()));
        assertEquals("You are not authorized for this action!", ex.getMessage());
    }

    @Test
    public void promoteUserOnChannel() {
        User admin = new User(UUID.randomUUID(), "admin", "pass", null, "USER");
        Membership membership = new Membership(user, openChannel, Role.user);
        when(membershipService.getPermissions(openChannel, admin)).thenReturn(2);
        when(userService.getUser(user.getUsername())).thenReturn(user);
        when(membershipService.getPermissions(openChannel, user)).thenReturn(0);
        when(membershipService.get(openChannel, user)).thenReturn(membership);
        assertDoesNotThrow(() -> channelManagementService.promoteUserOnChannel(openChannel, admin, user.getUsername()));
        assertEquals(Role.moderator, membership.getRole());
    }

    @Test
    public void promoteUserOnChannelNotAuthorized() {
        User admin = new User(UUID.randomUUID(), "admin", "pass", null, "USER");
        when(membershipService.getPermissions(openChannel, admin)).thenReturn(2);
        when(userService.getUser(user.getUsername())).thenReturn(user);
        when(membershipService.getPermissions(openChannel, user)).thenReturn(1);
        Exception ex = assertThrows(NotAuthorizedException.class, () -> channelManagementService.promoteUserOnChannel(openChannel, admin, user.getUsername()));
        assertEquals("Not authorized for this action!", ex.getMessage());    
    }

    @Test
    public void demoteUserOnChannel() {
        User admin = new User(UUID.randomUUID(), "admin", "pass", null, "USER");
        Membership membership = new Membership(user, openChannel, Role.moderator);
        when(membershipService.getPermissions(openChannel, admin)).thenReturn(2);
        when(userService.getUser(user.getUsername())).thenReturn(user);
        when(membershipService.getPermissions(openChannel, user)).thenReturn(1);
        when(membershipService.get(openChannel, user)).thenReturn(membership);
        assertDoesNotThrow(() -> channelManagementService.demoteUserOnChannel(openChannel, admin, user.getUsername()));
        assertEquals(Role.user, membership.getRole());
    }

    @Test
    public void demoteUserOnChannelNotAuthorized() {
        User admin = new User(UUID.randomUUID(), "admin", "pass", null, "USER");
        when(membershipService.getPermissions(openChannel, admin)).thenReturn(2);
        when(userService.getUser(user.getUsername())).thenReturn(user);
        when(membershipService.getPermissions(openChannel, user)).thenReturn(2);
        Exception ex = assertThrows(NotAuthorizedException.class, () -> channelManagementService.demoteUserOnChannel(openChannel, admin, user.getUsername()));
        assertEquals("Not authorized for this action!", ex.getMessage());    
    }

    @Test
    public void newArticle(){
        User admin = new User(UUID.randomUUID(), "admin", "pass", null, "USER");
        when(membershipService.checkPermissions(openChannel, admin, 2)).thenReturn(2);
        when(articleRepository.save(any(Article.class))).thenAnswer(i -> i.getArguments()[0]);
        Article article = new Article();
        article.setId(UUID.randomUUID());
        article.setChannel(openChannel);
        Article created = channelManagementService.newArticle(admin, article);
        article.setTimestamp(LocalDateTime.now());
        assertEquals(article, created);
    }

}
