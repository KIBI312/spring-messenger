package com.seitov.messenger.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.seitov.messenger.entity.Image;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.ImageRepository;

@Service
public class ImageService {
    
    private ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public byte[] getImageContent(UUID id) {
        Optional<Image> image = imageRepository.findById(id);
        if(image.isEmpty()) {
            throw new ResourceNotFoundException("Image not found");
        }
        return image.get().getContent();
    }

}
