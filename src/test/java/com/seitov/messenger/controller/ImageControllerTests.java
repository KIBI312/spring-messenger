package com.seitov.messenger.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.seitov.messenger.controller.rest.ImageController;
import com.seitov.messenger.entity.Image;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.service.ImageService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ImageControllerTests {
    
    @MockBean
    ImageService imageService;

    @InjectMocks
    ImageController imageController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getImage() throws Exception {
        Image image = new Image();
        image.setId(UUID.randomUUID());
        byte[] content = new byte[100];
        new Random().nextBytes(content);
        image.setContent(content);
        when(imageService.getImageContent(image.getId())).thenReturn(content);
        MvcResult mvcResult = mockMvc.perform(get("/image/{id}/", image.getId())).andExpect(status().isOk()).andReturn();
        assertTrue(Arrays.equals(content, mvcResult.getResponse().getContentAsByteArray()));
    }

    @Test
    public void getNonExistingImage() throws Exception {
        when(imageService.getImageContent(any(UUID.class))).thenThrow(new ResourceNotFoundException("Image not found!"));
        MvcResult mvcResult = mockMvc.perform(get("/image/{id}/", UUID.randomUUID())).andExpect(status().isNotFound()).andReturn();
        assertEquals("Image not found!", mvcResult.getResponse().getContentAsString());
    }

}
