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

import com.seitov.messenger.controller.rest.AttachmentController;
import com.seitov.messenger.entity.Attachment;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.service.AttachmentService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AttachmentControllerTests {
    
    @MockBean
    AttachmentService attachmentService;

    @InjectMocks
    AttachmentController attachmentController;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getAttachment() throws Exception {
        Attachment attachment = new Attachment();
        byte[] content = new byte[100];
        new Random().nextBytes(content);
        attachment.setId(UUID.randomUUID());
        attachment.setFilename("filename.pdf");
        attachment.setContent(content);
        when(attachmentService.get(attachment.getId())).thenReturn(attachment);
        MvcResult mvcResult = mockMvc.perform(get("/file/{id}", attachment.getId())).andExpect(status().isOk()).andReturn();
        assertTrue(Arrays.equals(attachment.getContent(), mvcResult.getResponse().getContentAsByteArray()));
        assertEquals("attachment; filename=\"" + attachment.getFilename() + "\"", mvcResult.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    public void getNonExistingAttachment() throws Exception {
        when(attachmentService.get(any(UUID.class))).thenThrow(new ResourceNotFoundException("Attachment with this id doesnt exist!"));
        MvcResult mvcResult = mockMvc.perform(get("/file/{id}", UUID.randomUUID())).andExpect(status().isNotFound()).andReturn();
        assertEquals("Attachment with this id doesnt exist!", mvcResult.getResponse().getContentAsString());
    }

}
