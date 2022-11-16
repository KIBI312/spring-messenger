package com.seitov.messenger.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seitov.messenger.dto.UpdatePasswordDto;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.exception.IllegalDataException;
import com.seitov.messenger.exception.IllegalDataFormatException;
import com.seitov.messenger.exception.ResourceNotFoundException;
import com.seitov.messenger.repository.UserRepository;

@Service
public class UserService {
    
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(String username) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new ResourceNotFoundException("User with this username doesnt exist!");
        }
        return user.get();
    }

    public Map<String, User> getUserPair(String first, String second) throws ResourceNotFoundException {
        Optional<User> firstUser = userRepository.findByUsername(first);
        Optional<User> secondUser = userRepository.findByUsername(second);
        if(firstUser.isEmpty() || secondUser.isEmpty()) {
            throw new ResourceNotFoundException("One of users doesnt exist!");
        }
        Map<String, User> userPair = new HashMap<>();
        userPair.put(first, firstUser.get());
        userPair.put(second, secondUser.get());
        return Collections.unmodifiableMap(userPair);
    }

    public User update(User user) throws IllegalDataFormatException, IllegalDataException {
        try {
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalDataFormatException("User for update must not be null!", e.getCause());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalDataFormatException("Illegal User entity received!", e.getCause());
        }
    }

    public User updatePassword(User user, UpdatePasswordDto updatePasswordDto) throws IllegalDataFormatException, IllegalDataException {
        if(!updatePasswordDto.getNewPassword().equals(updatePasswordDto.getMatchingPassword())){
            throw new IllegalDataException("New passwords are not matching");
        }
        if(!passwordEncoder.matches(updatePasswordDto.getPassword(), user.getPassword())){
            throw new IllegalDataException("Wrong old password. Access denied!");
        }
        user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
        try {
            return userRepository.save(user);   
        } catch (DataIntegrityViolationException e) {
            throw new IllegalDataFormatException("Illegal User entity received!", e.getCause());
        }
    }

}
