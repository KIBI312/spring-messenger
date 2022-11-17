package com.seitov.messenger.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Image;
import com.seitov.messenger.entity.Channel.AccessType;
import com.seitov.messenger.exception.IllegalDataFormatException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.ArticleRepository;
import com.seitov.messenger.repository.ChannelRepository;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTests {
    
    @Mock
    ChannelRepository channelRepository;
    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    ChannelService channelService;

    @Test
    public void createChannel() {
        Channel channel = new Channel(UUID.randomUUID(), "Channel", null, null, AccessType.closed);
        when(channelRepository.saveAndFlush(channel)).thenReturn(channel);
        assertEquals(channel, channelService.create(channel));
    }

    @Test
    public void createChannelWithNullRequiredFields() {
        Channel channel = new Channel(null, "Channel", new Image(), "description", null);
        when(channelRepository.saveAndFlush(channel)).thenThrow(DataIntegrityViolationException.class);
        Exception ex = assertThrows(IllegalDataFormatException.class, () -> channelService.create(channel));
        assertEquals("Name and accessType cannot be null!", ex.getMessage());
    }

    @Test
    public void getChannel() {
        Channel channel = new Channel(UUID.randomUUID(), "Channel", null, null, AccessType.closed);
        when(channelRepository.findById(channel.getId())).thenReturn(Optional.of(channel));
        assertEquals(channel, channelService.get(channel.getId()));
    }

    @Test
    public void getNonExistingChannel() {
        when(channelRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(null));
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> channelService.get(UUID.randomUUID()));
        assertEquals("Channel with such id doesnt exist", ex.getMessage());
    }

    @Test
    public void getOpenChannels() {
        List<Channel> channels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Channel channel = new Channel(UUID.randomUUID(), "channel"+i, null, null, AccessType.open);
            channels.add(channel);
        }
        when(channelRepository.findAllByAccessType(AccessType.open, PageRequest.of(0, 10))).thenReturn(channels);
        assertEquals(channels, channelService.getOpenChannels(PageRequest.of(0, 10)));
    } 

}
