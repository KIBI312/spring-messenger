package com.seitov.messenger.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.seitov.messenger.controller.rest.UserOperationsController;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.exception.IllegalDataException;
import com.seitov.messenger.security.UserPrincipal;
import com.seitov.messenger.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserOperationsControllerTests {
    
    @MockBean
    UserService userService;

    @InjectMocks
    UserOperationsController userOperationsController;

    @Autowired
    MockMvc mockMvc;

    private static Principal principal;

    @BeforeAll
    public static void setPrincipal() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        principal = new UsernamePasswordAuthenticationToken(new UserPrincipal(user), "password");
    }

    @Test
    public void updatePassword() throws Exception {
        MvcResult mvcResult = mockMvc.perform(multipart(HttpMethod.PATCH, "/user/update/password")
        .param("password", "password")
        .param("newPassword", "newPass")
        .param("matchingPassword", "newPass")
        .principal(principal))
        .andExpect(status().isOk())
        .andReturn();
        assertEquals("Your password was updated!", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void updatePasswordIllegalDto() throws Exception {
        when(userService.updatePassword(any(), any())).thenThrow(new IllegalDataException("New passwords are not matching"));
        MvcResult mvcResult = mockMvc.perform(multipart(HttpMethod.PATCH, "/user/update/password")
        .param("password", "password")
        .param("newPassword", "new")
        .param("matchingPassword", "newPass")
        .principal(principal))
        .andExpect(status().isBadRequest())
        .andReturn();
        assertEquals("New passwords are not matching", mvcResult.getResponse().getContentAsString());
    }

}
