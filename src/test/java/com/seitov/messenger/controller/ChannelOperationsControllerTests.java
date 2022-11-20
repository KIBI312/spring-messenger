package com.seitov.messenger.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.security.Principal;
import java.util.UUID;

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

import com.seitov.messenger.controller.rest.UserOperationsController;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.security.UserPrincipal;
import com.seitov.messenger.service.AuthService;
import com.seitov.messenger.service.ChannelManagementService;
import com.seitov.messenger.service.ChannelService;
import com.seitov.messenger.service.MembershipService;
import com.seitov.messenger.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ChannelOperationsControllerTests {
        
    @MockBean
    ChannelManagementService channelManagementService;
    @MockBean
    ChannelService channelService;
    @MockBean
    AuthService authService;
    @MockBean
    MembershipService membershipService;



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
    public void deleteChannel() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/channel/delete")
                            .content(UUID.randomUUID().toString())
                            .principal(principal))
                            .andExpect(status().isOk()).andReturn();
        assertEquals("Channel was deleted!", mvcResult.getResponse().getContentAsString());
    }

}
