package com.seitov.messenger.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.seitov.messenger.controller.rest.FriendshipController;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.exception.IllegalDataException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.security.UserPrincipal;
import com.seitov.messenger.service.FriendshipService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class FriendshipControllerTests {
    
    @MockBean
    FriendshipService friendshipService;

    @InjectMocks
    FriendshipController friendshipController;

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
    public void sendFriendRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/friend/add")
                            .content("friend").principal(principal))
                            .andExpect(status().isCreated())                        
                            .andReturn();
        assertEquals("Friend request sent!", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void sendFriendRequestDuplicate() throws Exception {
        when(friendshipService.sendFriendshipRequest(anyString(), anyString())).thenThrow(new IllegalDataException("Request to this user already sent!"));
        MvcResult mvcResult = mockMvc.perform(post("/friend/add")
                            .content("friend").principal(principal))
                            .andExpect(status().isBadRequest())                        
                            .andReturn();
        assertEquals("Request to this user already sent!", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void acceptFriendRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(patch("/friend/accept")
                            .content("friend").principal(principal))
                            .andExpect(status().isOk())                        
                            .andReturn();
        assertEquals("Friend request accepted!", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void acceptNonExistingFriendRequest() throws Exception {
        when(friendshipService.acceptFriendshipRequest(anyString(), anyString())).thenThrow(new ResourceNotFoundException("Friend request not found!"));
        MvcResult mvcResult = mockMvc.perform(patch("/friend/accept")
                            .content("friend").principal(principal))
                            .andExpect(status().isNotFound())                        
                            .andReturn();
        assertEquals("Friend request not found!", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void acceptIllegalFriendRequest() throws Exception {
        when(friendshipService.acceptFriendshipRequest(anyString(), anyString())).thenThrow(new IllegalDataException("This request is already active or blocked!"));
        MvcResult mvcResult = mockMvc.perform(patch("/friend/accept")
                            .content("friend").principal(principal))
                            .andExpect(status().isBadRequest())                        
                            .andReturn();
        assertEquals("This request is already active or blocked!", mvcResult.getResponse().getContentAsString());
    }

}
