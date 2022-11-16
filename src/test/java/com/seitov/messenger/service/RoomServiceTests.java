package com.seitov.messenger.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.seitov.messenger.entity.Channel;
import com.seitov.messenger.entity.Message;
import com.seitov.messenger.entity.Room;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.entity.Channel.AccessType;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.MessageRepository;
import com.seitov.messenger.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTests {

    @Mock
    RoomRepository roomRepository;
    @Mock
    MessageRepository messageRepository;
    @Mock
    MembershipService membershipService;

    private Channel openChannel = new Channel(UUID.randomUUID(), "openChannel", null, null, AccessType.open);
        

    @InjectMocks
    RoomService roomService;

    @Test
    public void getRoom() {
        Room room = new Room();
        room.setChannel(openChannel);
        room.setName("room");
        when(roomRepository.findByNameAndChannel(anyString(), any(Channel.class))).thenReturn(Optional.ofNullable(room));
        assertEquals(room, roomService.get(openChannel, room.getName()));
    }

    @Test
    public void getNonExistingRoom() {
        when(roomRepository.findByNameAndChannel(anyString(), any(Channel.class))).thenReturn(Optional.ofNullable(null));
        String roomName = "randomRoom";
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> roomService.get(openChannel, roomName));
        assertEquals("Room with name: "+ roomName + ", not found on this channel", ex.getMessage());
    }
    
    @Test
    public void newMessage() {
        Message message = new Message();
        Room room = new Room();
        room.setChannel(openChannel);
        message.setRoom(room);
        User user = new User(UUID.randomUUID(), "user", "pass", null, "USER");
        when(membershipService.checkPermissions(openChannel, user, 0)).thenReturn(0);
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);
        Message savedMessage = roomService.newMessage(user, message);
        message.setTimestamp(LocalDateTime.now());
        assertEquals(message, savedMessage);
    }

    @Test
    public void getMessage() {
        Message message = new Message();
        message.setId(UUID.randomUUID());
        when(messageRepository.findById(message.getId())).thenReturn(Optional.ofNullable(message));
        assertEquals(message, roomService.getMessage(message.getId()));
    }

    @Test
    public void getNonExistingMessage() {
        when(messageRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(null));
        Exception ex =assertThrows(ResourceNotFoundException.class, () -> roomService.getMessage(UUID.randomUUID()));
        assertEquals("Message with this id doesnt exist", ex.getMessage());
    }

    
}
