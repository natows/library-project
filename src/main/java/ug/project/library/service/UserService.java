package ug.project.library.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ug.project.library.dto.UserDto;
import ug.project.library.exceptions.UserNotFoundException;
import ug.project.library.model.entity.User;
import ug.project.library.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
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
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = getUserById(id);
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setUserRole(userDto.getUserRole());
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
