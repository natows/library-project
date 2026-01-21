package ug.project.library.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ug.project.library.dto.UserRegistrationDto;
import ug.project.library.exceptions.EmailAlreadyExistsException;
import ug.project.library.exceptions.UserNotFoundException;
import ug.project.library.exceptions.UsernameAlreadyExistsException;
import ug.project.library.model.entity.User;
import ug.project.library.repository.UserRepository;
import ug.project.library.model.enumerate.*;
import ug.project.library.dto.*;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String getCurrentUsername(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        
        String username = auth.getName();
        return username;

    }
    
    public Long getCurrentUserId() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));
        
        return user.getId();
    }

    public User getCurrentUser() {
        String username = getCurrentUsername() ;
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));
        
        return user;

    }

    private void verifyRegistrationData(UserRegistrationDto userRegistrationDto) {
        if (userRepository.findByUsername(userRegistrationDto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(userRegistrationDto.getUsername());
        }

        if (userRepository.findByEmail(userRegistrationDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(userRegistrationDto.getEmail());
        }

        if (userRegistrationDto.getPassword().length() < 6){
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    public User registerUser(UserRegistrationDto userRegistrationDto) {
        verifyRegistrationData(userRegistrationDto);
        User user = new User(userRegistrationDto.getUsername(),
            passwordEncoder.encode(userRegistrationDto.getPassword()),
            userRegistrationDto.getEmail(),
            UserRole.USER
        );

        User savedUser = userRepository.save(user);

        return savedUser;

    }
    
    
}