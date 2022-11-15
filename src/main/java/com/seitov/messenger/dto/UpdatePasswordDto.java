package com.seitov.messenger.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UpdatePasswordDto {
    
    @NotEmpty
    private String password;
    @NotEmpty
    private String newPassword;
    @NotEmpty
    private String matchingPassword;

}
