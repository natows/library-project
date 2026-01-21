package ug.project.library.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ug.project.library.controller.ReservationController;
import ug.project.library.dto.ReservationDto;
import ug.project.library.model.enumerate.ReservationStatus;
import ug.project.library.service.ReservationService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void shouldMakeReservation() throws Exception {
        ReservationDto reservation = createSampleReservationDto(1L, ReservationStatus.OCZEKUJĄCA);
        when(reservationService.createNewReservation(1L)).thenReturn(reservation);

        mockMvc.perform(post("/api/reservations/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OCZEKUJĄCA"));
    }

    @Test
    @WithMockUser
    public void shouldConfirmReservation() throws Exception {
        ReservationDto reservation = createSampleReservationDto(1L, ReservationStatus.POTWIERDZONA);
        when(reservationService.confirmReservation(1L)).thenReturn(reservation);

        mockMvc.perform(put("/api/reservations/1/confirm")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("POTWIERDZONA"));
    }

    @Test
    @WithMockUser
    public void shouldBorrowReservation() throws Exception {
        ReservationDto reservation = createSampleReservationDto(1L, ReservationStatus.WYPOŻYCZONA);
        when(reservationService.borrowReservation(1L)).thenReturn(reservation);

        mockMvc.perform(put("/api/reservations/1/borrow")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WYPOŻYCZONA"));
    }

    @Test
    @WithMockUser
    public void shouldReturnReservation() throws Exception {
        ReservationDto reservation = createSampleReservationDto(1L, ReservationStatus.ZWRÓCONA);
        when(reservationService.returnReservation(1L)).thenReturn(reservation);

        mockMvc.perform(put("/api/reservations/1/return")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ZWRÓCONA"));
    }

    @Test
    @WithMockUser
    public void shouldCancelReservation() throws Exception {
        mockMvc.perform(delete("/api/reservations/1/cancel")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    private ReservationDto createSampleReservationDto(Long id, ReservationStatus status) {
        return new ReservationDto(id, status, LocalDateTime.now(), LocalDateTime.now().plusDays(7), 1L, "user", 1L, "Book");
    }
}
