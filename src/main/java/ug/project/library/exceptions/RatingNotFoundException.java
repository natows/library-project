package ug.project.library.exceptions;


public class RatingNotFoundException extends RuntimeException{
    public RatingNotFoundException(Long id) {
        super("Rating with id " + id + " not found");
    }
}
