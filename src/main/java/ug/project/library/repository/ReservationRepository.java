package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.Reservation;
import ug.project.library.model.enumerate.ReservationStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    //tu pageable
    List<Reservation> findByStatus(ReservationStatus status); 

    Optional<Reservation> findByIdAndUserId(Long reservationId, Long userId);


    @Query("SELECT r FROM Reservation r WHERE r.status = 'OCZEKUJĄCA' AND r.deadline < :now")
    List<Reservation> findExpiredReservations(@Param("now") LocalDateTime now);

    
    
}
