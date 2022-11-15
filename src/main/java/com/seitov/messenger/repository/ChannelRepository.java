package com.seitov.messenger.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Channel.AccessType;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    Optional<Channel> findById(UUID id);
    List<Channel> findAllByAccessType(AccessType accessType, Pageable pageable);

}
