package com.seitov.messenger.controller.rest;

import java.io.IOException;
import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.seitov.messenger.dto.UpdatePasswordDto;
import com.seitov.messenger.entity.Image;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.service.UserService;
import com.seitov.messenger.util.ImageUtils;

@RestController
@RequestMapping("/user")
public class UserOperationsController {
    
    private UserService userService;

    @Autowired
    public UserOperationsController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping(path = "/update/profilePic", consumes = "multipart/form-data")
    public String updateUserProfilePicture(Principal principal, @RequestParam("image") MultipartFile image ) {
        User user = userService.getUser(principal.getName());
        Image img = new Image();
        try {
            img.setContent(ImageUtils.resize(image.getBytes(), 400));
        } catch (IOException e) {
            throw new RuntimeException("Error occured during resizing image", e.getCause());
        }
        user.setProfilePic(img);
        userService.update(user);
        return "Success";
    }

    @PatchMapping(path = "/update/password", consumes = "multipart/form-data")
    public String updateUserPassword(Principal principal, @Valid UpdatePasswordDto updatePasswordDto ) {
        User user = userService.getUser(principal.getName());
        userService.updatePassword(user, updatePasswordDto);
        return "Success";
    }

}
