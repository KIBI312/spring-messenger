package com.seitov.messenger.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.seitov.messenger.dto.MessageDto;
import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Message;
import com.seitov.messenger.entity.Room;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.exception.IllegalDataFormatException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.MessageRepository;
import com.seitov.messenger.repository.RoomRepository;

@Service
public class RoomService {
    
    private RoomRepository roomRepository;
    private MessageRepository messageRepository;
    private MembershipService membershipService;

    @Autowired
    public RoomService(RoomRepository roomRepository, MessageRepository messageRepository, MembershipService membershipService) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.membershipService = membershipService;
    }

    public Room create(Room room) throws IllegalDataFormatException {
        try {
            return roomRepository.saveAndFlush(room);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalDataFormatException("Error occured while attempting to create new room!", e.getCause());
        }
    }

    public Room get(Channel channel, String name) throws ResourceNotFoundException {
        Optional<Room> room = roomRepository.findByNameAndChannel(name, channel);
        if(room.isEmpty()) {
            throw new ResourceNotFoundException("Room with name: "+ name + ", not found on this channel");
        }
        return room.get();
    }

    public Set<Room> getChannelRooms(Channel channel) {
        Set<Room> rooms = roomRepository.findAllByChannel(channel);
        return Collections.unmodifiableSet(rooms);
    }

    public Message newMessage(User user, Message message) throws IllegalDataFormatException {
        membershipService.checkPermissions(message.getRoom().getChannel(), user, 0);
        message.setTimestamp(LocalDateTime.now());
        message.setUser(user);
        try { 
            return messageRepository.saveAndFlush(message);   
        } catch (DataIntegrityViolationException e) {
            throw new IllegalDataFormatException("Error occured while attempting to save new message!", e.getCause());
        }
    };

    public void deleteMessage(User user, Message message) {
        membershipService.checkPermissions(message.getRoom().getChannel(), user, 1);
        messageRepository.delete(message);
    };

    public Message getMessage(UUID id) throws ResourceNotFoundException {
        Optional<Message> message = messageRepository.findById(id);
        if(message.isEmpty()){
            throw new ResourceNotFoundException("Message with this id doesnt exist");
        }
        return message.get();
    }

    public List<MessageDto> getRoomMessages(Room room, Pageable pageable) {
        return messageRepository.findAllByRoomOrderByTimestampDesc(room, pageable);
    }

    public void deleteAllByChannel(Channel channel) {
        Set<Room> rooms = getChannelRooms(channel);
        rooms.forEach(room -> {
            messageRepository.deleteAllByRoom(room);
        });
        roomRepository.deleteAllByChannel(channel);
    }

}
