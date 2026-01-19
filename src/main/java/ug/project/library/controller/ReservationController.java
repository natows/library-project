package ug.project.library.controller;

import java.net.URI;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import ug.project.library.dto.ReservationDto;
import ug.project.library.service.ReservationService;
import ug.project.library.model.entity.Reservation;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Zarządzanie rezerwacjami książek")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @Operation(summary = "Utwórz nową rezerwację dla książki")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rezerwacja została utworzona"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono książki o podanym ID")
    })
    @PostMapping("/{bookId}")
    public ResponseEntity<ReservationDto> makeReservation(@PathVariable Long bookId) {
        ReservationDto reservation = reservationService.createNewReservation(bookId);
        return ResponseEntity.ok(reservation);
    }

    @Operation(summary = "Potwierdź rezerwację")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rezerwacja została potwierdzona"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono rezerwacji o podanym ID")
    })
    @PutMapping("/{reservationId}/confirm")
    public ResponseEntity<ReservationDto> confirmReservation(@PathVariable Long reservationId) {
        ReservationDto reservation = reservationService.confirmReservation(reservationId);
        return ResponseEntity.ok(reservation);
    }

    @Operation(summary = "Wypożycz książkę z rezerwacji")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Książka została wypożyczona"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono rezerwacji o podanym ID")
    })
    @PutMapping("/{reservationId}/borrow")
    public ResponseEntity<ReservationDto> borrowReservation(@PathVariable Long reservationId) {
        ReservationDto reservation = reservationService.borrowReservation(reservationId);
        return ResponseEntity.ok(reservation);
    }

    @Operation(summary = "Zwróć książkę z rezerwacji")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Książka została zwrócona"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono rezerwacji o podanym ID")
    })
    @PutMapping("/{reservationId}/return")
    public ResponseEntity<ReservationDto> returnReservation(@PathVariable Long reservationId) {
        ReservationDto reservation = reservationService.returnReservation(reservationId);
        return ResponseEntity.ok(reservation);
    }

    @Operation(summary = "Anuluj rezerwację")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Rezerwacja została anulowana"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono rezerwacji o podanym ID")
    })
    @DeleteMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
