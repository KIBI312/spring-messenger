package com.seitov.messenger.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.seitov.messenger.entity.Attachment;
import com.seitov.messenger.exception.IllegalDataFormatException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.AttachmentRepository;

@Service
public class AttachmentService {
    
    private AttachmentRepository attachmentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public Attachment create(Attachment attachment) throws IllegalDataFormatException {
        try {
            return attachmentRepository.save(attachment);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalDataFormatException("Attachment's filename must not be empty!", e.getCause());
        }
    }

    public Attachment get(UUID id) throws ResourceNotFoundException {
        Optional<Attachment> attachment = attachmentRepository.findById(id);
        if(attachment.isEmpty()) {
            throw new ResourceNotFoundException("Attachment with this id doesnt exist!");
        }
        return attachment.get();
    }

}
