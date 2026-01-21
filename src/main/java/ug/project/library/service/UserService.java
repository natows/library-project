package ug.project.library.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ug.project.library.dto.UserDto;
import ug.project.library.exceptions.EmailAlreadyExistsException;
import ug.project.library.exceptions.UsernameAlreadyExistsException;
import ug.project.library.exceptions.UserNotFoundException;
import ug.project.library.model.entity.User;
import ug.project.library.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(Long id) {
        return mapToDto(getUserById(id));
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public UserDto addUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(userDto.getEmail());
        }
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(userDto.getUsername());
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setUserRole(userDto.getUserRole());
        user.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));
        return mapToDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = getUserById(id);

        userRepository.findByEmail(userDto.getEmail())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(id)) {
                        throw new EmailAlreadyExistsException(userDto.getEmail());
                    }
                });

        userRepository.findByUsername(userDto.getUsername())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(id)) {
                        throw new UsernameAlreadyExistsException(userDto.getUsername());
                    }
                });

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setUserRole(userDto.getUserRole());
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        return mapToDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole()
        );
    }
}
