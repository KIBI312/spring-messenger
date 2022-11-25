package com.seitov.messenger.controller.mvc;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.seitov.messenger.dto.UserDto;
import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.service.AuthService;
import com.seitov.messenger.service.ChannelService;
import com.seitov.messenger.service.FriendshipService;
import com.seitov.messenger.service.MembershipService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    private FriendshipService friendshipService;
    private MembershipService membershipService;
    private ChannelService channelService;
    private AuthService authService;

    @Autowired
    public DashboardController(FriendshipService friendshipService, MembershipService membershipService, ChannelService channelService, AuthService authService){
        this.friendshipService = friendshipService;
        this.membershipService = membershipService;
        this.channelService = channelService;
        this.authService = authService;
    }

    @GetMapping("/{page}")
    public String getDashboard(Principal principal, Model model, HttpServletRequest request, @PathVariable int page, @RequestParam(required = false) String search) {
        Set<UserDto> friends = new HashSet<>();
        Set<UserDto> friendshipRequests = new HashSet<>();
        User user = authService.getUser(principal);
        Set<Channel> channels = membershipService.getMemberChannels(user);
        page = page<0 ? 0 : page;
        Pageable pageable = PageRequest.of(page, 15);
        List<Channel> openChannels;
        if (search != null && search.length()>0) {
            openChannels = channelService.searchOpenChannels(search, pageable);
            model.addAttribute("search", search);
        } else {
            openChannels = channelService.getOpenChannels(pageable);
        }
        friendshipService.getFriends(principal.getName()).forEach(e -> {
            friends.add(new UserDto(e.getUsername(), e.getProfilePic()));
        });
        friendshipService.getFriendshipRequests(principal.getName()).forEach(e -> {
            friendshipRequests.add(new UserDto(e.getUser().getUsername(), e.getUser().getProfilePic()));
        });
        model.addAttribute("username", principal.getName());
        model.addAttribute("friends", friends);
        model.addAttribute("friendshipRequests", friendshipRequests);
        model.addAttribute("channels", channels);
        model.addAttribute("openChannels", openChannels);
        model.addAttribute("page", page);
        return "dashboard";
    }

}
