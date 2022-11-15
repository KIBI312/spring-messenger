package com.seitov.messenger.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class RegistrationDto {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String matchingPassword;

}
