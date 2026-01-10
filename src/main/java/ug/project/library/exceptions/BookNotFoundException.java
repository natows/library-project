package ug.project.library.exceptions;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Book not found with id " + id);
    }
}
// to musi jakos zwrocic http 404 pamietaj