package ug.project.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ug.project.library.repository.RatingRepository;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Rating;
import ug.project.library.model.entity.User;
import ug.project.library.repository.*;
import ug.project.library.service.AuthService;
import ug.project.library.service.BookService;

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
    

    public RatingService(RatingRepository ratingRepository, AuthService authService, BookService bookService, BookRepository bookRepository) {
        this.ratingRepository = ratingRepository;
        this.authService = authService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }
    public Double getAverageRating(Long bookId) {
        Double avgRating = ratingRepository.calculateAverageRating(bookId);
        return avgRating;
    }

    private void checkRatingCooldown(Long userId) {
        LocalDateTime weekAgoDate = LocalDateTime.now().minus(RATING_COOLDOWN_DAYS, ChronoUnit.DAYS);

        List<Rating> recentRatings = ratingRepository.findRecentRatingsByUser(userId, weekAgoDate) ;

        if (!recentRatings.isEmpty()) {
            throw new IllegalStateException("Mozesz dodac ocene tylko raz na tydzien");
        }       

    }

    private void validateScoreValue(Integer score){
        if (score <1 || score >5) {
            throw new IllegalArgumentException("musi byc liczba od 0 do 5");
        }
    }

    public Rating addRating(Long bookId,  Integer score){
        validateScoreValue(score);

        User user = authService.getCurrentUser();
        Book book = bookService.getBookById(bookId);

        checkRatingCooldown(user.getId());

        Rating rating = new Rating(user, book, score);

        Rating savedRating = ratingRepository.save(rating);
        updateBookAverageRating(book.getId());

        return savedRating;
        

    }

    private void updateBookAverageRating(Long bookId){
        Double avgRating = ratingRepository.calculateAverageRating(bookId);
        Book book = bookService.getBookById(bookId);
        book.setAvgRating(avgRating != null ? avgRating :0.0);
        bookRepository.save(book);// czy tu nie powinno byc cos z service a nei z repository

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

    public Rating updateRating(Long ratingId, Integer newScore){
        validateScoreValue(newScore);

        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() -> new IllegalArgumentException());
        
        Long currentUserId = authService.getCurrentUserId();
        Long authorUserId = rating.getUser().getId();

        if (! currentUserId.equals(authorUserId)) {
            throw new IllegalArgumentException("nie jestes autorem opinii");
        }

        checkEditCooldown(rating);

        rating.setScore(newScore);
        rating.setLastModifiedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);
        updateBookAverageRating(savedRating.getBook().getId());

        return savedRating;
    }

    

}