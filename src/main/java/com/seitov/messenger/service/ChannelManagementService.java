package com.seitov.messenger.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seitov.messenger.entity.Article;
import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Membership;
import com.seitov.messenger.entity.Room;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Membership.Role;
import com.seitov.messenger.exception.IllegalDataException;
import com.seitov.messenger.exception.IllegalDataFormatException;
import com.seitov.messenger.exception.NotAuthorizedException;
import com.seitov.messenger.exception.ResourceAlreadyExistsException;
import com.seitov.messenger.repository.ArticleRepository;

@Service
@Transactional
public class ChannelManagementService {
    
    private ChannelService channelService;
    private MembershipService membershipService;
    private UserService userService;
    private ArticleRepository articleRepository;
    private RoomService roomService;

    @Autowired
    public ChannelManagementService(ChannelService channelService, MembershipService membershipService,
                 UserService userService, ArticleRepository articleRepository, RoomService roomService) {
        this.channelService = channelService;
        this.membershipService = membershipService;
        this.userService = userService;
        this.articleRepository = articleRepository;
        this.roomService = roomService;
    }

    public Channel createChannel(Channel channel, User user) {
        channelService.create(channel);
        membershipService.create(channel, user, Role.founder);
        return channel;
    }

    public void deleteChannel(Channel channel, User user) {
        membershipService.checkPermissions(channel, user, 3);
        membershipService.deleteAllByChannel(channel);
        articleRepository.deleteAllByChannel(channel);
        roomService.deleteAllByChannel(channel);
        channelService.delete(channel.getId());
    }

    public Channel update(Channel channel, User user) {
        membershipService.checkPermissions(channel, user, 2);
        return channelService.update(channel);
    }

    public Channel updateDetails(Channel channel, User user) {
        membershipService.checkPermissions(channel, user, 2);
        Channel original = channelService.get(channel.getId());
        original.setName(channel.getName());
        original.setDescription(channel.getDescription());
        original.setAccessType(channel.getAccessType());
        return channelService.update(original);
    }

    public Membership addUserToChannel(Channel channel, User admin, String username) {
        membershipService.checkPermissions(channel, admin, 1);
        User user = userService.getUser(username);
        return membershipService.create(channel, user, Role.user);
    }

    public void removeUserFromChannel(Channel channel, User admin, String username) throws NotAuthorizedException {
        int operatorPermissions = membershipService.getPermissions(channel, admin);
        User user = userService.getUser(username);
        int userPermissions = membershipService.getPermissions(channel, user);
        if(operatorPermissions<1 || operatorPermissions<=userPermissions) {
            throw new NotAuthorizedException("You are not authorized for this action!");
        }
        membershipService.delete(channel, user);
    }

    public void promoteUserOnChannel(Channel channel, User admin, String username) throws NotAuthorizedException, IllegalDataException {
        int operatorPermissions = membershipService.getPermissions(channel, admin);
        User user = userService.getUser(username);
        int userPermissions = membershipService.getPermissions(channel, user);
        if(operatorPermissions<2 || userPermissions>1 || operatorPermissions-userPermissions<=1) {
            throw new NotAuthorizedException("Not authorized for this action!");
        }
        Membership membership = membershipService.get(channel, user);
        switch (membership.getRole().getValue()) {
            case 0:
                membership.setRole(Role.moderator);
                break;
            case 1:
                membership.setRole(Role.admin);
                break;
            default:
                throw new IllegalDataException("Member have illegal role for promoting");
        }
        membershipService.update(membership);
    }

    public void demoteUserOnChannel(Channel channel, User admin, String username) throws NotAuthorizedException, IllegalDataException {
        int operatorPermissions = membershipService.getPermissions(channel, admin);
        User user = userService.getUser(username);
        int userPermissions = membershipService.getPermissions(channel, user);
        if(operatorPermissions<2 || userPermissions<1 || operatorPermissions<=userPermissions) {
            throw new NotAuthorizedException("Not authorized for this action!");
        }
        Membership membership = membershipService.get(channel, user);
        switch (membership.getRole().getValue()) {
            case 1:
                membership.setRole(Role.user);
                break;
            case 2:
                membership.setRole(Role.moderator);
                break;
            default:
                throw new IllegalDataException("Member have illegal role for promoting");
        }
        membershipService.update(membership);
    }

    public Article newArticle(User user, Article article) throws IllegalDataFormatException {
        membershipService.checkPermissions(article.getChannel(), user, 2);
        article.setTimestamp(LocalDateTime.now());
        try {
            return articleRepository.save(article);   
        } catch (Exception e) {
            throw new IllegalDataFormatException("Title cannot be null!");
        }
    }

    public Room newRoom(User user, Room room) throws ResourceAlreadyExistsException {
        membershipService.checkPermissions(room.getChannel(), user, 2);
        try {
            return roomService.create(room);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("Room with this name already exists on this channel!");
        }
    }

}
