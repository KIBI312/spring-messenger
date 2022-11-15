package com.seitov.messenger.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.seitov.messenger.entity.User;
import com.seitov.messenger.repository.UserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private  UserRepository userRepository;

    @Autowired
    public AppUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("No user found with this username: " + username);
        }
        return new UserPrincipal(user.get());
    }
    
}
