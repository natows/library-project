package ug.project.library.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ug.project.library.model.entity.Reservation;
import ug.project.library.service.ReservationService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationExpirationScheduler {

    private final ReservationService reservationService;

    public ReservationExpirationScheduler(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @Scheduled(fixedRate = 60000) 
    public void expireReservations() {

        reservationService.expireReservations();
    }
}
