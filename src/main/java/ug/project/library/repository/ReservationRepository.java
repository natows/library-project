package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.Reservation;
import ug.project.library.model.enumerate.ReservationStatus;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByStatus(ReservationStatus status); 

    
    
}
