package ug.project.library.service;

import ug.project.library.repository.ReservationRepository;
import ug.project.library.service.AuthService;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ug.project.library.dto.BookDto;
import ug.project.library.dto.BookDto;
import ug.project.library.exceptions.BookNotFoundException;
import ug.project.library.model.entity.*;
import ug.project.library.model.enumerate.ReservationStatus;
import ug.project.library.repository.BookRepository;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@Transactional
public class ReservationService {


    private final ReservationRepository reservationRepository;
    private final BookService bookService;
    private final UserService userService;
    private final AuthService authService;

    public ReservationService(ReservationRepository reservationRepository,BookService bookService, UserService userService, AuthService authService){
        this.reservationRepository = reservationRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.authService = authService;
    }



    public Reservation createNewReservation(Long bookId){
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User must be logged in to make a reservation");
        }// gdy niezalogowany ma odsylac do logowania narzie bedzie to
        Book book = bookService.getBookById(bookId);
        bookService.deincrementQuantityAvailable(book);

        Reservation reservation = new Reservation(ReservationStatus.OCZEKUJĄCA, LocalDateTime.now(), user, book );
        
        return reservationRepository.save(reservation);
    }


}