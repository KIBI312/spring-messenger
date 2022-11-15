package com.seitov.messenger.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seitov.messenger.entity.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    
}
