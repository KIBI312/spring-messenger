package com.seitov.messenger.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seitov.messenger.dto.RegistrationDto;
import com.seitov.messenger.entity.User;
import com.seitov.messenger.exception.UserRegistrationFailureException;
import com.seitov.messenger.repository.UserRepository;
import com.seitov.messenger.security.UserPrincipal;

@Service
public class AuthService {
    
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegistrationDto registrationDto) throws UserRegistrationFailureException {
        if(userRepository.findByUsername(registrationDto.getUsername()).isPresent()){
            throw new UserRegistrationFailureException("User with this username already exists!");
        } else if(!registrationDto.getPassword().equals(registrationDto.getMatchingPassword())){
            throw new UserRegistrationFailureException("Passwords are not matching");
        }
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public User getUser(Principal principal) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        return user.getUser();
    }


}
