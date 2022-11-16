package com.seitov.messenger.dto;

import com.seitov.messenger.entity.Image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private String username;
    private Image profilePic;

}
