package com.seitov.messenger.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seitov.messenger.entity.Article;
import com.seitov.messenger.entity.Channel;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    List<Article> findAllByChannelOrderByTimestampDesc(Channel channel, Pageable pageable);
    long deleteAllByChannel(Channel channel);
}
