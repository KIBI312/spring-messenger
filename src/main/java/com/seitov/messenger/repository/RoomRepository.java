package com.seitov.messenger.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    
    Set<Room> findAllByChannel(Channel channel);
    Optional<Room> findByNameAndChannel(String name, Channel channel);
    long deleteAllByChannel(Channel channel);

}
