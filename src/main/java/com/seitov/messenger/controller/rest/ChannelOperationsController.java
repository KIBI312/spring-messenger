package com.seitov.messenger.controller.rest;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.seitov.messenger.dto.MemberDto;
import com.seitov.messenger.entity.Article;
import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Image;
import com.seitov.messenger.entity.Room;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Channel.AccessType;
import com.seitov.messenger.entity.Membership.Role;
import com.seitov.messenger.exception.IllegalDataFormatException;
import com.seitov.messenger.exception.NotAuthorizedException;
import com.seitov.messenger.service.AuthService;
import com.seitov.messenger.service.ChannelManagementService;
import com.seitov.messenger.service.ChannelService;
import com.seitov.messenger.service.MembershipService;
import com.seitov.messenger.util.ImageUtils;

@RestController
@RequestMapping("/channel")
public class ChannelOperationsController {
    
    private  ChannelManagementService channelManagementService;
    private AuthService authService;
    private ChannelService channelService;
    private MembershipService membershipService;

    @Autowired
    public ChannelOperationsController(ChannelManagementService channelManagementService, AuthService authService, ChannelService channelService, MembershipService membershipService) {
        this.channelManagementService = channelManagementService;
        this.authService = authService;
        this.channelService = channelService;
        this.membershipService = membershipService;
    }

    @PostMapping(path = "/create", consumes = "multipart/form-data")
    public String createChannel(Principal principal, Channel channel, @RequestParam("image") MultipartFile image) {
        User user = authService.getUser(principal);
        if(!image.isEmpty()) {
            Image img = new Image();
            channel.setProfilePic(img);
            try {
                img.setContent(ImageUtils.resize(image.getBytes(), 128));
            } catch (IOException e) {
                throw new RuntimeException("Error occured during resizing image", e.getCause());
            }
        } else {
            throw new IllegalDataFormatException("Channel profile picture cannot be null!");
        }
        channelManagementService.createChannel(channel, user);
        return "success";
    }

    @DeleteMapping(path = "/delete")
    public String deleteChannel(Principal principal, @RequestBody String channelId) {
        User user = authService.getUser(principal);
        Channel channel = channelService.get(UUID.fromString(channelId));
        channelManagementService.deleteChannel(channel, user);
        return "success";
    }

    @PatchMapping(path = "/update/profilePic", consumes = "multipart/form-data")
    public String updateChannelProfilePicture(Principal principal, @RequestParam("channelId") UUID channelId, @RequestParam("image") MultipartFile image) {
        User user = authService.getUser(principal);
        Channel channel = channelService.get(channelId);
        if(!image.isEmpty()) {
            Image img = new Image();
            channel.setProfilePic(img);
            try {
                img.setContent(ImageUtils.resize(image.getBytes(), 128));
            } catch (IOException e) {
                throw new RuntimeException("Error occured during resizing image", e.getCause());
            }
        } else {
            throw new IllegalDataFormatException("Channel profile picture cannot be null!");
        }
        channelManagementService.update(channel, user);
        return "success";
    }

    @PatchMapping(path = "/update/details")
    public String updateChannelDetails(Principal principal, Channel channel) {
        User user = authService.getUser(principal);
        channelManagementService.updateDetails(channel, user);
        return "success";
    }

    @PutMapping(path = "/add/member", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String addMember(Principal principal, @RequestBody MemberDto memberDto) {
        User user = authService.getUser(principal);
        Channel channel = channelService.get(memberDto.getChannelId());
        channelManagementService.addUserToChannel(channel, user, memberDto.getUsername());   
        return "success";
    }

    @DeleteMapping(path = "/remove/member", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String removeMember(Principal principal, @RequestBody MemberDto memberDto) {
        User user = authService.getUser(principal);
        Channel channel = channelService.get(memberDto.getChannelId());
        channelManagementService.removeUserFromChannel(channel, user, memberDto.getUsername());
        return "success";
    }

    @PatchMapping(path = "/promote/member", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String promoteMember(Principal principal, @RequestBody MemberDto memberDto) {
        User user = authService.getUser(principal);
        Channel channel = channelService.get(memberDto.getChannelId());
        channelManagementService.promoteUserOnChannel(channel, user, memberDto.getUsername());
        return "success";
    }

    @PatchMapping(path = "/demote/member", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String demoteMember(Principal principal, @RequestBody MemberDto memberDto) {        
        User user = authService.getUser(principal);
        Channel channel = channelService.get(memberDto.getChannelId());
        channelManagementService.demoteUserOnChannel(channel, user, memberDto.getUsername());
        return "success";
    }

    @PutMapping(path = "/join")
    public String joinChannel(Principal principal, @RequestBody String channelId) {
        Channel channel = channelService.get(UUID.fromString(channelId));
        User user = authService.getUser(principal);
        if(channel.getAccessType()==AccessType.closed) {
            throw new NotAuthorizedException("Cannot join to private channel");
        }
        membershipService.create(channel, user, Role.user);
        return "success";
    }

    @DeleteMapping(path = "/leave")
    public String leaveChannel(Principal principal, @RequestBody String channelId) {
        Channel channel = channelService.get(UUID.fromString(channelId));
        User user = authService.getUser(principal);
        if(membershipService.getPermissions(channel, user)==3) {
            throw new NotAuthorizedException("Founder cannot leave channel, only delete it!");
        }
        membershipService.delete(channel, user);
        return "success";
    }

    @PostMapping(path = "/article/create", consumes = "multipart/form-data")
    public String newArticle(Principal principal, Article article, @RequestParam("image") MultipartFile image) {
        User user = authService.getUser(principal);
        if(!image.isEmpty()) {
            Image img = new Image();
            try {
                img.setContent(ImageUtils.resize(image.getBytes(), 800));
            } catch (IOException e) {
                throw new RuntimeException("Error occured during resizing image", e.getCause());
            }
            article.setArticleImage(img);
        }
        channelManagementService.newArticle(user, article);
        return "success";
    }

    @PostMapping(path = "/room/create")
    public String newRoom(Principal principal, Room room) {
        User user = authService.getUser(principal);
        channelManagementService.newRoom(user, room);
        return "success";
    }



}
