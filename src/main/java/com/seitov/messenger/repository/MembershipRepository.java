package com.seitov.messenger.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seitov.messenger.entity.Membership;
import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Membership.Role;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    
    Optional<Membership> findByUserAndChannel(User user, Channel channel);
    long deleteByUserAndChannel(User user, Channel channel);
    long deleteByChannel(Channel channel);
    Set<Membership> findByUser(User user);
    Set<Membership> findByChannel(Channel channel);
    Set<Membership> findByChannelAndRole(Channel chanel, Role role);
    
}
