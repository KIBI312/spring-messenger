package com.seitov.messenger.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seitov.messenger.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    
}
