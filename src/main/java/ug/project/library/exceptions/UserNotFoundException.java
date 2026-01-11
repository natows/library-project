package ug.project.library.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(Long id) {
        super("User not found with id " + id);
    }
    
    public UserNotFoundException(String username) {
        super("User not found with username: " + username);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}