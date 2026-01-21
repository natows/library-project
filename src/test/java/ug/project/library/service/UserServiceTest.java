package ug.project.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import ug.project.library.dto.UserDto;
import ug.project.library.exceptions.EmailAlreadyExistsException;
import ug.project.library.exceptions.UsernameAlreadyExistsException;
import ug.project.library.exceptions.UserNotFoundException;
import ug.project.library.model.entity.User;
import ug.project.library.model.enumerate.UserRole;
import ug.project.library.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password", "test@example.com", UserRole.USER);
        user.setId(1L);

        userDto = new UserDto(1L, "testuser", "test@example.com", UserRole.USER);
    }

    @Test
    @DisplayName("getAllUsers should return page of DTOs")
    void getAllUsers_ShouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user)));

        Page<UserDto> result = userService.getAllUsers(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("getUserById should return user when found")
    void getUserById_ShouldReturnUser_WhenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("getUserById should throw exception when not found")
    void getUserById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    @DisplayName("updateUser should update and return DTO")
    void updateUser_ShouldUpdateAndReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserDto updateDto = new UserDto(1L, "updated", "updated@example.com", UserRole.ADMIN);
        UserDto result = userService.updateUser(1L, updateDto);

        assertThat(result.getUsername()).isEqualTo("updated");
        assertThat(result.getUserRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("addUser should save and return DTO")
    void addUser_ShouldSaveAndReturnDto() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        UserDto newDto = new UserDto(null, "newuser", "new@example.com", UserRole.USER);
        newDto.setPassword("password123");
        
        UserDto result = userService.addUser(newDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("newuser");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("addUser should throw exception when email exists")
    void addUser_ShouldThrowException_WhenEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDto newDto = new UserDto(null, "newuser", "test@example.com", UserRole.USER);
        
        assertThrows(EmailAlreadyExistsException.class, () -> userService.addUser(newDto));
    }

    @Test
    @DisplayName("addUser should throw exception when username exists")
    void addUser_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDto newDto = new UserDto(null, "testuser", "new@example.com", UserRole.USER);
        
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.addUser(newDto));
    }

    @Test
    @DisplayName("deleteUser should delete when exists")
    void deleteUser_ShouldDelete_WhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser should throw exception when not exists")
    void deleteUser_ShouldThrowException_WhenNotExists() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }
}
