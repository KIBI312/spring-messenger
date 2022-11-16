package com.seitov.messenger.controller.mvc;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.seitov.messenger.dto.MessageDto;
import com.seitov.messenger.dto.UserDto;
import com.seitov.messenger.entity.Article;
import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Room;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Channel.AccessType;
import com.seitov.messenger.entity.Membership.Role;
import com.seitov.messenger.exception.NotAuthorizedException;
import com.seitov.messenger.service.AuthService;
import com.seitov.messenger.service.ChannelService;
import com.seitov.messenger.service.MembershipService;
import com.seitov.messenger.service.RoomService;

@Controller
public class ChannelController {

    private ChannelService channelService;
    private MembershipService membershipService;
    private AuthService authService;
    private RoomService roomService;

    @Autowired
    public ChannelController(ChannelService channelService, MembershipService membershipService, AuthService authService, RoomService roomService) {
        this.channelService = channelService;
        this.membershipService = membershipService;
        this.authService = authService;
        this.roomService = roomService;
    }
    
    @GetMapping("/channel/{id}/{page}")
    public String getChannel(Principal principal, Model model, @PathVariable UUID id, @PathVariable int page) {
        Channel channel = channelService.get(id);
        User user = authService.getUser(principal);
        int permissions = membershipService.getPermissions(channel, user);
        if(channel.getAccessType()==AccessType.closed && permissions==-1) {
            throw new NotAuthorizedException("Not authorized for this action");
        }
        page = page<0 ? 0 : page;
        Set<UserDto> members = membershipService.getChannelMembers(channel);
        Set<Room> rooms = roomService.getChannelRooms(channel);
        Pageable pageable = PageRequest.of(page, 10);
        List<Article> articles = channelService.getChannelArticles(channel, pageable);
        model.addAttribute("channel", channel);
        model.addAttribute("permissions", permissions);
        model.addAttribute("members", members);
        model.addAttribute("rooms", rooms);
        model.addAttribute("articles", articles);
        model.addAttribute("page", page);
        return "channel";
    }

    @GetMapping("/channel/{id}/settings")
    public String getChannelSettings(Principal principal, Model model, @PathVariable UUID id) {
        Channel channel = channelService.get(id);
        int permissions = membershipService.checkPermissions(channel, authService.getUser(principal),2);
        Set<UserDto> users = membershipService.getChannelMembers(channel, Role.user);
        Set<UserDto> moderators = membershipService.getChannelMembers(channel, Role.moderator);
        Set<UserDto> admins = membershipService.getChannelMembers(channel, Role.admin);
        model.addAttribute("channel", channel);
        model.addAttribute("users", users);
        model.addAttribute("moderators", moderators);
        model.addAttribute("admins", admins);
        model.addAttribute("permissions", permissions);
        return "channelSettings";
    }

    @GetMapping("/channel/{id}/room/{name}/{page}")
    public String getRoom(Principal principal, Model model, @PathVariable UUID id, @PathVariable String name, @PathVariable int page) {
        Channel channel = channelService.get(id);
        User user = authService.getUser(principal);
        int permissions = membershipService.getPermissions(channel, user);
        if(channel.getAccessType()==AccessType.closed && permissions==-1) {
            throw new NotAuthorizedException("Not authorized for this action");
        }
        Set<Room> rooms = roomService.getChannelRooms(channel);
        Room room = roomService.get(channel, name);
        page = page<0 ? 0 : page;
        Pageable pageable = PageRequest.of(page, 30);
        List<MessageDto> messages = roomService.getRoomMessages(room, pageable);
        model.addAttribute("user", user);
        model.addAttribute("channel", channel);
        model.addAttribute("room", room);
        model.addAttribute("permissions", permissions);
        model.addAttribute("rooms", rooms);
        model.addAttribute("messages", messages);
        model.addAttribute("page", page);
        return "room";
    }
    
}
