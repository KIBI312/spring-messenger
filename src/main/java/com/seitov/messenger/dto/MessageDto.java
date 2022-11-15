package com.seitov.messenger.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MessageDto {
    
    UUID getId();
    String getUsername();
    UUID getUserProfilePic();
    UUID getRoomId();
    UUID getAttachmentId();
    String getAttachmentFilename();
    String getText();
    LocalDateTime getTimestamp();

}
