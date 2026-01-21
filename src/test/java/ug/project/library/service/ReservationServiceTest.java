package ug.project.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ug.project.library.dao.ReservationDao;
import ug.project.library.dto.ReservationDto;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Reservation;
import ug.project.library.model.entity.User;
import ug.project.library.model.enumerate.ReservationStatus;
import ug.project.library.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private BookService bookService;
    @Mock
    private AuthService authService;
    @Mock
    private ReservationDao reservationDao;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private Book book;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setQuantityAvailable(5);

        reservation = new Reservation(ReservationStatus.OCZEKUJĄCA, LocalDateTime.now(), user, book, LocalDateTime.now().plusHours(2));
        reservation.setId(1L);
    }

    @Test
    @DisplayName("createNewReservation should create and return DTO")
    void createNewReservation_ShouldCreateAndReturnDto() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(bookService.getBookById(1L)).thenReturn(book);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationDto result = reservationService.createNewReservation(1L);

        assertThat(result).isNotNull();
        verify(bookService).deincrementQuantityAvailable(book);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("confirmReservation should update status when valid")
    void confirmReservation_ShouldUpdateStatus_WhenValid() {
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationDto result = reservationService.confirmReservation(1L);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.POTWIERDZONA);
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("confirmReservation should throw exception when deadline passed")
    void confirmReservation_ShouldThrowException_WhenDeadlinePassed() {
        reservation.setDeadline(LocalDateTime.now().minusHours(1));
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class, () -> reservationService.confirmReservation(1L));
        verify(bookService).incrementQuantityAvailable(book);
        verify(reservationRepository).delete(reservation);
    }

    @Test
    @DisplayName("borrowReservation should update status when confirmed")
    void borrowReservation_ShouldUpdateStatus_WhenConfirmed() {
        reservation.setStatus(ReservationStatus.POTWIERDZONA);
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationDto result = reservationService.borrowReservation(1L);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WYPOŻYCZONA);
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("returnReservation should update status and increment quantity")
    void returnReservation_ShouldUpdateStatusAndIncrementQuantity() {
        reservation.setStatus(ReservationStatus.WYPOŻYCZONA);
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationDto result = reservationService.returnReservation(1L);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.ZWRÓCONA);
        verify(bookService).incrementQuantityAvailable(book);
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("cancelReservation should delete and increment quantity")
    void cancelReservation_ShouldDeleteAndIncrementQuantity() {
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(1L);

        verify(bookService).incrementQuantityAvailable(book);
        verify(reservationRepository).delete(reservation);
    }

    @Test
    @DisplayName("getAllReservations should return list of DTOs")
    void getAllReservations_ShouldReturnListOfDtos() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<ReservationDto> result = reservationService.getAllReservations();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getUserReservationHistory should return page of entities")
    void getUserReservationHistory_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(reservationRepository.findPastReservations(1L, pageable)).thenReturn(new PageImpl<>(List.of(reservation)));

        Page<Reservation> result = reservationService.getUserReservationHistory(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("getUserActiveReservations should return list")
    void getUserActiveReservations_ShouldReturnList() {
        when(reservationDao.findActiveReservationsByUserId(1L)).thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getUserActiveReservations(1L);

        assertThat(result).hasSize(1);
    }
}
