package com.seitov.messenger.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.seitov.messenger.entity.Article;
import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Channel.AccessType;
import com.seitov.messenger.exception.IllegalDataFormatException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.ArticleRepository;
import com.seitov.messenger.repository.ChannelRepository;

@Service
public class ChannelService {

    private ChannelRepository channelRepository;
    private ArticleRepository articleRepository;

    @Autowired
    public ChannelService(ChannelRepository channelRepository, ArticleRepository articleRepository) {
        this.channelRepository = channelRepository;
        this.articleRepository = articleRepository;
    }

    public Channel create(Channel toCreate) throws IllegalDataFormatException {
        try {
            return channelRepository.saveAndFlush(toCreate);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalDataFormatException("Name and accessType cannot be null!");
        }
    }

    public Channel get(UUID id) throws ResourceNotFoundException {
        Optional<Channel> channel = channelRepository.findById(id);
        if(channel.isEmpty()) {
            throw new ResourceNotFoundException("Channel with such id doesnt exist");
        }
        return channel.get();
    }

    public Channel update(Channel channel) throws IllegalDataFormatException {
        try {
            return channelRepository.saveAndFlush(channel);   
        } catch (DataIntegrityViolationException e) {
            throw new IllegalDataFormatException("Error occured while attempting to update this channel!", e.getCause());
        }
    }

    public void delete(UUID id) {
        channelRepository.deleteById(id);
    }

    public List<Channel> getOpenChannels(Pageable pageable) {
        List<Channel> openChannels = channelRepository.findAllByAccessType(AccessType.open, pageable);
        return Collections.unmodifiableList(openChannels);
    }

    public List<Article> getChannelArticles(Channel channel, Pageable pageable) {
        return articleRepository.findAllByChannelOrderByTimestampDesc(channel, pageable);
    }

    public List<Channel> searchOpenChannels(String search, Pageable pageable) {
        List<Channel> channels = channelRepository.findAllByAccessTypeAndNameContainingIgnoreCase(AccessType.open, search, pageable);
        return Collections.unmodifiableList(channels);
    }

}
