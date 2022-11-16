package com.seitov.messenger.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.seitov.messenger.entity.Image;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.ImageRepository;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTests {
    
    @Mock
    ImageRepository imageRepository;

    @InjectMocks
    ImageService imageService;

    @Test
    public void getImageContent() {
        Image image = new Image();
        byte[] b = new byte[100];
        new Random().nextBytes(b);
        image.setContent(b);
        when(imageRepository.findById(image.getId())).thenReturn(Optional.ofNullable(image));
        assertEquals(b, imageService.getImageContent(image.getId()));
    }   

    @Test
    public void getNonExistingImage() {
        Image nonExistingImage = new Image();
        when(imageRepository.findById(nonExistingImage.getId())).thenReturn(Optional.ofNullable(null));
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> imageService.getImageContent(nonExistingImage.getId()));
        assertEquals("Image not found", ex.getMessage());
    }


}
