package ug.project.library.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ug.project.library.dto.RatingDto;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Rating;
import ug.project.library.model.entity.User;
import ug.project.library.repository.BookRepository;
import ug.project.library.repository.RatingRepository;
import ug.project.library.repository.ReservationRepository;
import ug.project.library.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class RatingService {
    private static final long RATING_COOLDOWN_DAYS = 7;
    private final RatingRepository ratingRepository;
    private final AuthService authService;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, AuthService authService, BookService bookService, 
                         BookRepository bookRepository, ReservationRepository reservationRepository,
                         UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.authService = authService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<RatingDto> getAllRatings(Pageable pageable) {
        return ratingRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public RatingDto getRatingDtoById(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
        return mapToDto(rating);
    }

    public Double getAverageRating(Long bookId) {
        return ratingRepository.calculateAverageRating(bookId);
    }

    private void checkRatingCooldown(Long userId) {
        LocalDateTime weekAgoDate = LocalDateTime.now().minus(RATING_COOLDOWN_DAYS, ChronoUnit.DAYS);
        List<Rating> recentRatings = ratingRepository.findRecentRatingsByUser(userId, weekAgoDate);
        if (!recentRatings.isEmpty()) {
            throw new IllegalStateException("Mozesz dodac ocene tylko raz na tydzien");
        }
    }

    private void validateScoreValue(Integer score){
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("musi byc liczba od 1 do 5");
        }
    }
    
    private void checkIfUserLoanedBook(Long bookId, Long userId) {
        if (!reservationRepository.existsLoanByBookIdAndUserId(bookId, userId)){
            throw new IllegalStateException("mozna oceniac tylko uprzednio wypozyczone ksiazki");
        }
    }

    public RatingDto addRating(RatingDto ratingDto){
        User user = userRepository.findById(ratingDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookService.getBookById(ratingDto.getBookId());

        checkIfUserLoanedBook(book.getId(), user.getId());
        validateScoreValue(ratingDto.getScore());
        checkRatingCooldown(user.getId());

        Rating rating = new Rating(user, book, ratingDto.getScore());
        Rating savedRating = ratingRepository.save(rating);
        updateBookAverageRating(book.getId());

        return mapToDto(savedRating);
    }

    private void updateBookAverageRating(Long bookId){
        Double avgRating = ratingRepository.calculateAverageRating(bookId);
        Book book = bookService.getBookById(bookId);
        book.setAvgRating(avgRating != null ? avgRating : 0.0);
        bookRepository.save(book);
    }

    private void checkEditCooldown(Rating rating) {
        if (rating.getLastModifiedAt() == null) {
            return; 
        }
        LocalDateTime weekAgo = LocalDateTime.now().minus(RATING_COOLDOWN_DAYS, ChronoUnit.DAYS);
        if (rating.getLastModifiedAt().isAfter(weekAgo)) {
            throw new IllegalStateException("Możesz edytować ocenę tylko raz na tydzień");
        }
    }

    public RatingDto updateRating(Long ratingId, RatingDto ratingDto){
        validateScoreValue(ratingDto.getScore());
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));
        
        checkEditCooldown(rating);

        rating.setScore(ratingDto.getScore());
        rating.setLastModifiedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);
        updateBookAverageRating(savedRating.getBook().getId());

        return mapToDto(savedRating);
    }

    @Transactional
    public void deleteRating(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
        Long bookId = rating.getBook().getId();
        ratingRepository.deleteById(id);
        updateBookAverageRating(bookId);
    }

    private RatingDto mapToDto(Rating rating) {
        return new RatingDto(
                rating.getId(),
                rating.getUser().getId(),
                rating.getUser().getUsername(),
                rating.getBook().getId(),
                rating.getBook().getTitle(),
                rating.getScore(),
                rating.getCreatedAt(),
                rating.getLastModifiedAt()
        );
    }
}