package ug.project.library.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ug.project.library.dto.BookDto;
import ug.project.library.dto.BookDto;
import ug.project.library.exceptions.*;
import ug.project.library.model.entity.*;
import ug.project.library.repository.BookRepository;
import ug.project.library.repository.UserRepository;

import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Transactional(readOnly = true)
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

    }

    
}
