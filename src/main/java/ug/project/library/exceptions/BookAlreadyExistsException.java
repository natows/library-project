package ug.project.library.exceptions;



public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String title) {
        super("Book with title '" + title + "' already exists in the catalog.");
    }
}
