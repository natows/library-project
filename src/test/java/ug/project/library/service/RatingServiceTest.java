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
import ug.project.library.dto.RatingDto;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Rating;
import ug.project.library.model.entity.User;
import ug.project.library.repository.BookRepository;
import ug.project.library.repository.RatingRepository;
import ug.project.library.repository.ReservationRepository;
import ug.project.library.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private BookService bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;

    @InjectMocks
    private RatingService ratingService;

    private User user;
    private Book book;
    private Rating rating;
    private RatingDto ratingDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        rating = new Rating(user, book, 5);
        rating.setId(1L);

        ratingDto = new RatingDto();
        ratingDto.setId(1L);
        ratingDto.setUserId(1L);
        ratingDto.setBookId(1L);
        ratingDto.setScore(5);
    }

    @Test
    @DisplayName("getAllRatings should return page of DTOs")
    void getAllRatings_ShouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(ratingRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(rating)));

        Page<RatingDto> result = ratingService.getAllRatings(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getScore()).isEqualTo(5);
    }

    @Test
    @DisplayName("addRating should save and return DTO when valid")
    void addRating_ShouldSaveAndReturnDto_WhenValid() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(bookService.getBookById(1L)).thenReturn(book);
        when(ratingRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        when(reservationRepository.existsLoanByBookIdAndUserId(1L, 1L)).thenReturn(true);
        when(ratingRepository.findRecentRatingsByUser(eq(1L), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(ratingRepository.calculateAverageRating(1L)).thenReturn(4.5);

        RatingDto result = ratingService.addRating(ratingDto);

        assertThat(result.getScore()).isEqualTo(5);
        verify(ratingRepository).save(any(Rating.class));
        verify(bookRepository).save(book);
        assertThat(book.getAvgRating()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("addRating should throw exception when user has not loaned the book")
    void addRating_ShouldThrowException_WhenNotLoaned() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(bookService.getBookById(1L)).thenReturn(book);
        when(ratingRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        when(reservationRepository.existsLoanByBookIdAndUserId(1L, 1L)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> ratingService.addRating(ratingDto));
    }

    @Test
    @DisplayName("addRating should throw exception when score is invalid")
    void addRating_ShouldThrowException_WhenScoreInvalid() {
        ratingDto.setScore(10); // RatingService expects 1-5
        when(authService.getCurrentUser()).thenReturn(user);
        when(bookService.getBookById(1L)).thenReturn(book);
        when(ratingRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        when(reservationRepository.existsLoanByBookIdAndUserId(1L, 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> ratingService.addRating(ratingDto));
    }

    @Test
    @DisplayName("addRating should throw exception when in cooldown")
    void addRating_ShouldThrowException_WhenInCooldown() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(bookService.getBookById(1L)).thenReturn(book);
        when(ratingRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        when(reservationRepository.existsLoanByBookIdAndUserId(1L, 1L)).thenReturn(true);
        when(ratingRepository.findRecentRatingsByUser(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(rating));

        assertThrows(IllegalStateException.class, () -> ratingService.addRating(ratingDto));
    }

    @Test
    @DisplayName("updateRating should update when not in cooldown")
    void updateRating_ShouldUpdate_WhenValid() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(ratingRepository.calculateAverageRating(1L)).thenReturn(4.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        ratingDto.setScore(4);
        RatingDto result = ratingService.updateRating(1L, ratingDto);

        assertThat(result.getScore()).isEqualTo(4);
        verify(ratingRepository).save(rating);
    }

    @Test
    @DisplayName("updateRating should throw exception when in edit cooldown")
    void updateRating_ShouldThrowException_WhenInEditCooldown() {
        rating.setLastModifiedAt(LocalDateTime.now().minusDays(1));
        when(authService.getCurrentUser()).thenReturn(user);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        assertThrows(IllegalStateException.class, () -> ratingService.updateRating(1L, ratingDto));
    }

    @Test
    @DisplayName("deleteRating should delete and update average")
    void deleteRating_ShouldDeleteAndUpdateAverage() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingRepository.calculateAverageRating(1L)).thenReturn(3.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        ratingService.deleteRating(1L);

        verify(ratingRepository).deleteById(1L);
        verify(bookRepository).save(book);
        assertThat(book.getAvgRating()).isEqualTo(3.0);
    }
}
