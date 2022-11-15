package com.seitov.messenger.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.seitov.messenger.dto.RegistrationDto;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.exception.UserRegistrationFailureException;
import com.seitov.messenger.repository.UserRepository;
import com.seitov.messenger.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    
    @Mock
    UserRepository userRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    AuthService authService;

    @Test
    public void register() {
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setUsername("user");
        registrationDto.setPassword("password");
        registrationDto.setMatchingPassword("password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        User user = authService.register(registrationDto);
        assertEquals("user", user.getUsername());
        assertEquals("USER", user.getRole());
        assertTrue(passwordEncoder.matches("password", user.getPassword()));
    }

    @Test
    public void registerWithNonMatchingPasswords() {
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setUsername("user");
        registrationDto.setPassword("password");
        registrationDto.setMatchingPassword("passwor");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));
        Exception ex = assertThrows(UserRegistrationFailureException.class, () -> authService.register(registrationDto));
        assertEquals("Passwords are not matching", ex.getMessage());
    }

    @Test
    public void registerWithTakenUsername() {
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setUsername("user");
        registrationDto.setPassword("password");
        registrationDto.setMatchingPassword("passwor");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));
        Exception ex = assertThrows(UserRegistrationFailureException.class, () -> authService.register(registrationDto));
        assertEquals("User with this username already exists!", ex.getMessage());
    }

    @Test
    public void getUser() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        Principal principal = new UsernamePasswordAuthenticationToken(new UserPrincipal(user), "password");
        assertEquals(user, authService.getUser(principal)); 
    }

} 
