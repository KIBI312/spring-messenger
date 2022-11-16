package com.seitov.messenger.controller.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seitov.messenger.service.ImageService;

@RestController
@RequestMapping("/image")
public class ImageController {
    
    private ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    private Resource getImage(@PathVariable UUID imageId) {
        byte[] image = imageService.getImageContent(imageId);
        return new ByteArrayResource(image);
    }

}
