package ug.project.library.model.entity;

import ug.project.library.model.enumerate.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime createdAt;

    private User user;

    private Book book;



    
}
