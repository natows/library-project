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
import ug.project.library.dto.BookDto;
import ug.project.library.service.BookService;
import ug.project.library.model.entity.*;





@RestController
@RequestMapping("api/reservation")
public class ReservationController {
    private ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<Reservation> makeReservation(@PathVariable Long bookId) {
        Reservation reservation = reservationService.createNewReservation(bookId);
        return ResponseEntity.ok(reservation);
    }


}