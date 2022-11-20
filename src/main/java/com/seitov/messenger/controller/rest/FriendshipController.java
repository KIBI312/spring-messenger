package com.seitov.messenger.controller.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seitov.messenger.service.FriendshipService;

@RestController
@RequestMapping("/friend")
public class FriendshipController {
    
    private  FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService){
        this.friendshipService = friendshipService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> sendFriendRequest(Principal principal, @RequestBody String username) {
        friendshipService.sendFriendshipRequest(principal.getName(), username);   
        return ResponseEntity.created(null).body("Friend request sent!");
    }

    @PatchMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest(Principal principal, @RequestBody String username){
        friendshipService.acceptFriendshipRequest(username, principal.getName());
        return ResponseEntity.ok("Friend request accepted!");
    }

}
