package com.seitov.messenger.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.seitov.messenger.dto.UpdatePasswordDto;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.exception.IllegalDataException;
import com.seitov.messenger.exception.IllegalDataFormatException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    
    @Mock
    UserRepository userRepository;
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    UserService userService;

    @Test
    public void getNonExistingUser() {
        String username = "username";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));
        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(username));
    }

    @Test
    public void getExistingUser() {
        String username = "username";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUser(username));
    }

    @Test
    public void getUserPair() {
        User user = new User();
        user.setUsername("firstUser");
        User secondUser = new User();
        secondUser.setUsername("secondUser");
        when(userRepository.findByUsername("firstUser")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("secondUser")).thenReturn(Optional.of(secondUser));
        Map<String, User> result = userService.getUserPair("firstUser", "secondUser");
        assertEquals(result.get("firstUser"), user);
        assertEquals(result.get("secondUser"), secondUser);
    }

    @Test
    public void getNonExistingUserPair() {
        User secondUser = new User();
        secondUser.setUsername("secondUser");
        when(userRepository.findByUsername("firstUser")).thenReturn(Optional.ofNullable(null));
        when(userRepository.findByUsername("secondUser")).thenReturn(Optional.of(secondUser));
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserPair("firstUser", "secondUser")) ;
    }

    @Test
    public void updateUserPassword() {
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();
        updatePasswordDto.setPassword("pass");
        updatePasswordDto.setNewPassword("newPass");
        updatePasswordDto.setMatchingPassword("newPass");
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("pass"));
        updatePasswordDto.setMatchingPassword("newPass");
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(user, userService.updatePassword(user, updatePasswordDto));
    }

    @Test
    public void updateUserPasswordsNotMatching() {
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();
        updatePasswordDto.setPassword("pas");
        updatePasswordDto.setNewPassword("newPass");
        updatePasswordDto.setMatchingPassword("newPass");
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("pass"));
        Exception ex = assertThrows(IllegalDataException.class, () -> userService.updatePassword(user, updatePasswordDto));
        assertEquals("Wrong old password. Access denied!", ex.getMessage());
    }

    @Test
    public void updateUserPasswordWrongOldPassword() {
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();
        updatePasswordDto.setPassword("pas");
        updatePasswordDto.setNewPassword("newPass");
        updatePasswordDto.setMatchingPassword("nonMatch");
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("pass"));
        Exception ex = assertThrows(IllegalDataException.class, () -> userService.updatePassword(user, updatePasswordDto));
        assertEquals(ex.getMessage(), "New passwords are not matching");
    }

    @Test
    public void updateUser() {
        User illegalUser = new User();
        illegalUser.setUsername("");
        illegalUser.setPassword("");
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        when(userRepository.save(null)).thenThrow(IllegalArgumentException.class);
        when(userRepository.save(illegalUser)).thenThrow(DataIntegrityViolationException.class);
        Exception ex;
        ex = assertThrows(IllegalDataFormatException.class, () -> userService.update(null));
        assertEquals("User for update must not be null!", ex.getMessage());
        ex = assertThrows(IllegalDataFormatException.class, () -> userService.update(illegalUser));
        assertEquals("Illegal User entity received!", ex.getMessage());
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(user, userService.update(user));
    }

}
