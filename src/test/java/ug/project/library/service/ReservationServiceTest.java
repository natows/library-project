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
        verify(authService).getCurrentUser();
        verify(bookService).getBookById(1L);
        verify(bookService).deincrementQuantityAvailable(book);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("createNewReservation should throw exception when user not logged in")
    void createNewReservation_ShouldThrowException_WhenUserNotLoggedIn() {
        when(authService.getCurrentUser()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> reservationService.createNewReservation(1L));
        verify(authService).getCurrentUser();
        verifyNoInteractions(bookService);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("confirmReservation should update status when valid")
    void confirmReservation_ShouldUpdateStatus_WhenValid() {
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationDto result = reservationService.confirmReservation(1L);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.POTWIERDZONA);
        verify(authService).getCurrentUserId();
        verify(reservationRepository).findByIdAndUserId(1L, 1L);
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("confirmReservation should throw exception when status not OCZEKUJĄCA")
    void confirmReservation_ShouldThrowException_WhenStatusNotOczekujaca() {
        reservation.setStatus(ReservationStatus.POTWIERDZONA);
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class, () -> reservationService.confirmReservation(1L));
        verify(authService).getCurrentUserId();
        verify(reservationRepository).findByIdAndUserId(1L, 1L);
        verify(reservationRepository, never()).save(any());
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
        verify(authService).getCurrentUserId();
        verify(reservationRepository).findByIdAndUserId(1L, 1L);
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("borrowReservation should throw exception when status not POTWIERDZONA")
    void borrowReservation_ShouldThrowException_WhenStatusNotPotwierdzona() {
        reservation.setStatus(ReservationStatus.OCZEKUJĄCA);
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class, () -> reservationService.borrowReservation(1L));
        verify(reservationRepository, never()).save(any());
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
        verify(authService).getCurrentUserId();
        verify(reservationRepository).findByIdAndUserId(1L, 1L);
        verify(bookService).incrementQuantityAvailable(book);
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("returnReservation should throw exception when status not WYPOŻYCZONA")
    void returnReservation_ShouldThrowException_WhenStatusNotWypozyczona() {
        reservation.setStatus(ReservationStatus.POTWIERDZONA);
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class, () -> reservationService.returnReservation(1L));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancelReservation should delete and increment quantity when status is OCZEKUJĄCA")
    void cancelReservation_ShouldDeleteAndIncrementQuantity_WhenOczekujaca() {
        reservation.setStatus(ReservationStatus.OCZEKUJĄCA);
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(1L);

        verify(authService).getCurrentUserId();
        verify(reservationRepository).findByIdAndUserId(1L, 1L);
        verify(bookService).incrementQuantityAvailable(book);
        verify(reservationRepository).delete(reservation);
    }

    @Test
    @DisplayName("cancelReservation should delete and increment quantity when status is POTWIERDZONA")
    void cancelReservation_ShouldDeleteAndIncrementQuantity_WhenPotwierdzona() {
        reservation.setStatus(ReservationStatus.POTWIERDZONA);
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(1L);

        verify(authService).getCurrentUserId();
        verify(reservationRepository).findByIdAndUserId(1L, 1L);
        verify(bookService).incrementQuantityAvailable(book);
        verify(reservationRepository).delete(reservation);
    }

    @Test
    @DisplayName("cancelReservation should throw exception when status is WYPOŻYCZONA")
    void cancelReservation_ShouldThrowException_WhenWypozyczona() {
        reservation.setStatus(ReservationStatus.WYPOŻYCZONA);
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class, () -> reservationService.cancelReservation(1L));
        verify(bookService, never()).incrementQuantityAvailable(any());
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("cancelReservation should throw exception when status is ZWRÓCONA")
    void cancelReservation_ShouldThrowException_WhenZwrocona() {
        reservation.setStatus(ReservationStatus.ZWRÓCONA);
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class, () -> reservationService.cancelReservation(1L));
        verify(bookService, never()).incrementQuantityAvailable(any());
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllReservations should return list of DTOs")
    void getAllReservations_ShouldReturnListOfDtos() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<ReservationDto> result = reservationService.getAllReservations();

        assertThat(result).hasSize(1);
        verify(reservationRepository).findAll();
    }

    @Test
    @DisplayName("getUserReservationHistory should return page of entities")
    void getUserReservationHistory_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(reservationRepository.findPastReservations(1L, pageable)).thenReturn(new PageImpl<>(List.of(reservation)));

        Page<Reservation> result = reservationService.getUserReservationHistory(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(reservationRepository).findPastReservations(1L, pageable);
    }

    @Test
    @DisplayName("getUserActiveReservations should return list")
    void getUserActiveReservations_ShouldReturnList() {
        when(reservationDao.findActiveReservationsByUserId(1L)).thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getUserActiveReservations(1L);

        assertThat(result).hasSize(1);
        verify(reservationDao).findActiveReservationsByUserId(1L);
    }

    @Test
    @DisplayName("getReservationById should return reservation when found")
    void getReservationById_ShouldReturnReservation_WhenFound() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result = reservationService.getReservationById(1L);

        assertThat(result).isEqualTo(reservation);
        verify(reservationRepository).findById(1L);
    }

    @Test
    @DisplayName("getReservationById should throw exception when not found")
    void getReservationById_ShouldThrowException_WhenNotFound() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ug.project.library.exceptions.ReservationNotFoundError.class, () -> reservationService.getReservationById(1L));
        verify(reservationRepository).findById(1L);
    }

    @Test
    @DisplayName("getReservationByIdAndUserId should throw exception when not found")
    void getReservationByIdAndUserId_ShouldThrowException_WhenNotFound() {
        when(reservationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reservationService.getReservationByIdAndUserId(1L, 1L));
        verify(reservationRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("expireReservations should expire all found reservations")
    void expireReservations_ShouldExpireAllFound() {
        List<Reservation> expiredList = List.of(reservation);
        when(reservationRepository.findExpiredReservations(any(LocalDateTime.class))).thenReturn(expiredList);

        reservationService.expireReservations();

        verify(reservationRepository).findExpiredReservations(any(LocalDateTime.class));
        verify(bookService).incrementQuantityAvailable(book);
        verify(reservationRepository).delete(reservation);
    }
}
