package com.seitov.messenger.controller.rest;

import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seitov.messenger.entity.Attachment;
import com.seitov.messenger.service.AttachmentService;

@RestController
@RequestMapping("/file")
public class AttachmentController {

    private AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> get(@PathVariable UUID id) {
        Attachment file = attachmentService.get(id);
        Resource res = new ByteArrayResource(file.getContent());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"").body(res);
    }

}
