package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.Rating;
import ug.project.library.model.enumerate.ReservationStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUserIdAndBookId(Long userId, Long bookId);

    Page<Rating> findByBookId(Long bookId, Pageable pageable);


    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.book.id = :bookId")
    Double calculateAverageRating(@Param("bookId") Long bookId); 

    @Query("SELECT r from Rating r WHERE r.user.id = :userId AND r.createdAt > :since")
    List<Rating> findRecentRatingsByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);





    
    
}
