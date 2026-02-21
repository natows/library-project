package ug.project.library.exceptions;

public class ReservationNotFoundError extends RuntimeException{
    public ReservationNotFoundError(Long id) {
        super("Reservation not found with id " + id);
    }
}
