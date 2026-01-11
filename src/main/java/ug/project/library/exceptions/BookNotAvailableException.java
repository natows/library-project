package ug.project.library.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) 
public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(String bookTitle,Long bookId) {
        super(String.format("Brak dostępnych egzemplarzy książki (Tytuł: %s) (ID: %d)",bookTitle, bookId));
    }

    public BookNotAvailableException(String message) {
        super(message);
    }
}
