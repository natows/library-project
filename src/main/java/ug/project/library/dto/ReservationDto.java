package ug.project.library.dto;

import java.time.LocalDateTime;
import ug.project.library.model.enumerate.ReservationStatus;

public class ReservationDto {
    private Long id;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    
    private Long userId;
    private String userName;
    
    private Long bookId;
    private String bookTitle;

    public ReservationDto() {
    }

    public ReservationDto(Long id, ReservationStatus status, LocalDateTime createdAt, 
                         LocalDateTime deadline, Long userId, String userName, 
                         Long bookId, String bookTitle) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.userId = userId;
        this.userName = userName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setdeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
}