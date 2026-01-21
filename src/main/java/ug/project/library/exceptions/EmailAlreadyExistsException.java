package ug.project.library.exceptions;


public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String  email) {
        super("The email '" + email + "' is taken");

    }
}
