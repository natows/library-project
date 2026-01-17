package ug.project.library.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ug.project.library.service.ReservationService;
import ug.project.library.dto.ReservationDto;
import ug.project.library.service.BookService;
import ug.project.library.model.entity.*;





@RestController
@RequestMapping("api/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<ReservationDto> makeReservation(@PathVariable Long bookId) {
        ReservationDto reservation = reservationService.createNewReservation(bookId);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{reservationId}/confirm")
    public ResponseEntity<ReservationDto> confirmReservation(@PathVariable Long reservationId) {
        ReservationDto reservation = reservationService.confirmReservation(reservationId);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{reservationId}/borrow")
    public ResponseEntity<ReservationDto> borrowReservation(@PathVariable Long reservationId) {
        ReservationDto reservation = reservationService.borrowReservation(reservationId);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{reservationId}/return")
    public ResponseEntity<ReservationDto> returnReservation(@PathVariable Long reservationId) {
        ReservationDto reservation = reservationService.returnReservation(reservationId);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }




}