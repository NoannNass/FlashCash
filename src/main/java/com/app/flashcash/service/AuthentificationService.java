package com.app.flashcash.service;

import com.app.flashcash.entity.User;
import com.app.flashcash.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AuthentificationService implements UserDetailsService {


    private final UserRepository userRepository;

    public AuthentificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
      Optional<User> user = userRepository
              .findByEmail(s);
      if(user.isPresent()) {
      return new org.springframework.security.core.userdetails.User(user.get().getEmail(),
              user.get().getPassword(),
              new ArrayList<>());
      }
      throw new UsernameNotFoundException(s);
    }
}
