package ug.project.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ug.project.library.dto.UserRegistrationDto;
import ug.project.library.exceptions.EmailAlreadyExistsException;
import ug.project.library.exceptions.UserNotFoundException;
import ug.project.library.exceptions.UsernameAlreadyExistsException;
import ug.project.library.model.entity.User;
import ug.project.library.model.enumerate.UserRole;
import ug.project.library.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "encodedPassword", "test@example.com", UserRole.USER);
        user.setId(1L);

        registrationDto = new UserRegistrationDto("testuser", "password123", "test@example.com");
    }

    @Test
    @DisplayName("getCurrentUserId should return ID when authenticated")
    void getCurrentUserId_ShouldReturnId_WhenAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("testuser");
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Long result = authService.getCurrentUserId();

        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("getCurrentUserId should throw exception when not authenticated")
    void getCurrentUserId_ShouldThrowException_WhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(UserNotFoundException.class, () -> authService.getCurrentUserId());
    }

    @Test
    @DisplayName("registerUser should save user when data is valid")
    void registerUser_ShouldSaveUser_WhenDataIsValid() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.registerUser(registrationDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser should throw exception when username exists")
    void registerUser_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(UsernameAlreadyExistsException.class, () -> authService.registerUser(registrationDto));
    }

    @Test
    @DisplayName("registerUser should throw exception when email exists")
    void registerUser_ShouldThrowException_WhenEmailExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class, () -> authService.registerUser(registrationDto));
    }

    @Test
    @DisplayName("registerUser should throw exception when password too short")
    void registerUser_ShouldThrowException_WhenPasswordTooShort() {
        registrationDto.setPassword("123");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(registrationDto));
    }
}
