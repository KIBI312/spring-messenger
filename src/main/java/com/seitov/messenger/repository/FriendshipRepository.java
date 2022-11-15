package com.seitov.messenger.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seitov.messenger.entity.Friendship;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Friendship.Status;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID>{
    
    Optional<Friendship> findByUserAndFriend(User user, User friend);
    Set<Friendship> findByUserAndStatus(User user, Status status);
    Set<Friendship> findByFriendAndStatus(User friend, Status status);

}
