package com.seitov.messenger.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.seitov.messenger.entity.Attachment;
import com.seitov.messenger.entity.Message;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.service.AuthService;
import com.seitov.messenger.service.RoomService;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/channel/room/message")
public class MessagingController {
    
    private RoomService roomService;
    private AuthService authService;

    @Autowired 
    public MessagingController(RoomService roomService, AuthService authService) {
        this.roomService = roomService;
        this.authService = authService;
        
    }

    @PostMapping(value="/create", consumes = "multipart/form-data")
    public String newMessage(Principal principal, Message message, @RequestParam("file") MultipartFile file) {
        User user = authService.getUser(principal);
        if(!file.isEmpty()){
            Attachment attachment = new Attachment();
            try {
                attachment.setContent(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error occured during reading file!", e.getCause());
            }
            attachment.setFilename(file.getOriginalFilename());
            message.setAttachment(attachment);
        }
        roomService.newMessage(user, message);
        return "success";
    }
    
    @DeleteMapping(value="/remove")
    public String deleteMessage(Principal principal, @RequestBody String messageId) {
        User user = authService.getUser(principal);
        Message message = roomService.getMessage(UUID.fromString(messageId));
        roomService.deleteMessage(user, message);
        return "success";
    }


}
