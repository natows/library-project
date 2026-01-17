package ug.project.library.service;

import ug.project.library.repository.ReservationRepository;
import ug.project.library.service.AuthService;
import ug.project.library.service.BookService;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ug.project.library.dao.ReservationDao;
import ug.project.library.dto.ReservationDto;
import ug.project.library.exceptions.BookNotFoundException;
import ug.project.library.model.entity.*;
import ug.project.library.model.enumerate.ReservationStatus;
import ug.project.library.repository.BookRepository;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.Duration;
import ug.project.library.dto.*;
 


@Service
@Transactional
public class ReservationService {
    //to gdzies przeniesc w logiczniejsze miejsce(ale nie enum!!)
    private static final Duration CONFIRMATION_TIME = Duration.ofHours(2);

    private final ReservationRepository reservationRepository;
    private final ug.project.library.service.BookService bookService;
    private final UserService userService;
    private final AuthService authService;
    private final ReservationDao reservationDao;

    public ReservationService(ReservationRepository reservationRepository,BookService bookService, UserService userService, AuthService authService, ReservationDao reservationDao){
        this.reservationRepository = reservationRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.authService = authService;
        this.reservationDao = reservationDao;
    }

    public Reservation getReservationByIdAndUserId(Long reservationId, Long userId) {
        return reservationRepository.findByIdAndUserId(reservationId, userId).orElseThrow(() -> new IllegalArgumentException());
        
    }

    private ReservationDto mapReservationToDto(Reservation reservation) {
        return new ReservationDto(
            reservation.getId(),
            reservation.getStatus(),
            reservation.getCreatedAt(),
            reservation.getDeadline(),
            reservation.getUser().getId(),
            reservation.getUser().getUsername(),
            reservation.getBook().getId(),
            reservation.getBook().getTitle()            
        );
    }

    // private Reservation mapDtoToReservation(ReservationDto reservationDto) {
    //     User user = userService.getUserById(reservationDto.getUserId());
    //     Book book = bookService.getBookById(reservationDto.getBookId());
    //     return new Reservation(
    //         reservationDto.getStatus(),
    //         reservationDto.getCreatedAt(),
    //         user,
    //         book,
    //         reservationDto.getConfirmationDeadline()
    //     );


    // }

    @Transactional
    public List<Reservation> getUserActiveReservations(Long userId){
        return reservationDao.findActiveReservationsByUserId(userId);
    }

    @Transactional
    public ReservationDto createNewReservation(Long bookId){
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User must be logged in to make a reservation");
        }// gdy niezalogowany ma odsylac do logowania narzie bedzie to
        Book book = bookService.getBookById(bookId);
        bookService.deincrementQuantityAvailable(book);

        LocalDateTime confirmationDeadline = LocalDateTime.now().plus(CONFIRMATION_TIME);

        Reservation reservation = new Reservation(ReservationStatus.OCZEKUJĄCA, LocalDateTime.now(), user, book, confirmationDeadline);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        return mapReservationToDto(savedReservation);
    }


    @Transactional
    public ReservationDto confirmReservation(Long reservationId){
        Long userId = authService.getCurrentUserId();

        Reservation reservation = getReservationByIdAndUserId(reservationId, userId);

        if (reservation.getStatus() != ReservationStatus.OCZEKUJĄCA) {
            throw new IllegalStateException("Nie można potwierdzić tej rezerwacji");
        }

        if (reservation.getDeadline().isBefore(LocalDateTime.now())) {
            expireReservation(reservation);
            throw new IllegalStateException();

        }

        reservation.setStatus(ReservationStatus.POTWIERDZONA);

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapReservationToDto(savedReservation);

    }

    @Transactional
    public void expireReservation(Reservation reservation){
        Book book = reservation.getBook();
        bookService.incrementQuantityAvailable(book);
        reservationRepository.delete(reservation);
    }

    @Transactional
    public void expireReservations(){
        List<Reservation> expired =
                reservationRepository.findExpiredReservations(LocalDateTime.now());

        for (Reservation reservation : expired) {
            expireReservation(reservation);
        }

    }

    @Transactional
    public ReservationDto borrowReservation(Long reservationId) {
        Long userId = authService.getCurrentUserId();
        Reservation reservation = getReservationByIdAndUserId(reservationId, userId);
        if (reservation.getStatus() != ReservationStatus.POTWIERDZONA) {
            throw new IllegalStateException("Można wypożyczyć tylko potwierdzoną rezerwację");
        }

        reservation.setStatus(ReservationStatus.WYPOŻYCZONA);
        Reservation savedReservation = reservationRepository.save(reservation);
        return mapReservationToDto(savedReservation);
    }

    @Transactional 
    public ReservationDto returnReservation(Long reservationId) {
        Long userId = authService.getCurrentUserId();
        Reservation reservation = getReservationByIdAndUserId(reservationId, userId);

        if (reservation.getStatus() != ReservationStatus.WYPOŻYCZONA) {
            throw new IllegalStateException("Można zwrócić tylko wypożyczoną książkę");
        }
        
        reservation.setStatus(ReservationStatus.ZWRÓCONA);
        bookService.incrementQuantityAvailable(reservation.getBook());
        Reservation savedReservation = reservationRepository.save(reservation);
        return mapReservationToDto(savedReservation);

    }


    @Transactional
    public void cancelReservation(Long reservationId){
        Long userId = authService.getCurrentUserId();
        Reservation reservation = getReservationByIdAndUserId(reservationId, userId);

        if (reservation.getStatus() != ReservationStatus.OCZEKUJĄCA && 
            reservation.getStatus() != ReservationStatus.POTWIERDZONA) {
            throw new IllegalStateException("Można anulować tylko oczekującą lub potwierdzoną rezerwację");
        }


        bookService.incrementQuantityAvailable(reservation.getBook());
        reservationRepository.delete(reservation);

    }

    


}