package com.seitov.messenger.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.seitov.messenger.dto.MessageDto;
import com.seitov.messenger.entity.Message;
import com.seitov.messenger.entity.Room;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID>{

    @Query("SELECT a.id as id, a.text as text, a.timestamp as timestamp, a.room.id as roomId, " +
    "b.username as username, b.profilePic.id as userProfilePic, c.id as attachmentId, " +
    "c.filename as attachmentFilename FROM Message a LEFT JOIN a.user b " +
    "LEFT JOIN a.attachment c " +
    "WHERE a.room = :room ORDER BY timestamp DESC")
    List<MessageDto> findAllByRoomOrderByTimestampDesc(@Param("room") Room room, Pageable pageable);

}


