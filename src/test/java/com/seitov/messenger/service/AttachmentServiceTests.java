package com.seitov.messenger.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.seitov.messenger.entity.Attachment;
import com.seitov.messenger.exception.IllegalDataFormatException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.AttachmentRepository;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTests {
    
    @Mock
    AttachmentRepository attachmentRepository;

    @InjectMocks
    AttachmentService attachmentService;

    @Test
    public void getAttachment() {
        Attachment attachment = new Attachment();
        when(attachmentRepository.findById(attachment.getId())).thenReturn(Optional.of(attachment));
        assertEquals(attachment, attachmentService.get(attachment.getId()));
    }

    @Test
    public void getNonExistingAttachment() {
        UUID randomId = UUID.randomUUID();
        when(attachmentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(null));
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> attachmentService.get(randomId));
        assertEquals("Attachment with this id doesnt exist!", ex.getMessage());
    }

    @Test
    public void createWithEmptyFilename() {
        Attachment attachment = new Attachment();
        attachment.setFilename("");
        when(attachmentRepository.save(attachment)).thenThrow(DataIntegrityViolationException.class);
        Exception ex = assertThrows(IllegalDataFormatException.class, () -> attachmentService.create(attachment));
        assertEquals("Attachment's filename must not be empty!", ex.getMessage());
    }

    @Test
    public void createWithValidFilename() {
        Attachment attachment = new Attachment();
        attachment.setFilename("filename.pdf");
        when(attachmentRepository.save(attachment)).thenReturn(attachment);
        assertEquals(attachmentService.create(attachment), attachment);
    }

}
