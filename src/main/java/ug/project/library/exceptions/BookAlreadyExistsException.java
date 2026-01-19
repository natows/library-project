package ug.project.library.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String title) {
        super("Book with title '" + title + "' already exists in the catalog.");
    }
}
