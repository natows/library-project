package ug.project.library.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.*;


public class RatingDto {
    private Long id;

    @NotNull(message = "Id użytkownika jest wymagane")
    private Long userId;

    private String username;

    @NotNull(message = "Id książki jest wymagane")
    private Long bookId;

    private String bookTitle;

    @NotNull(message = "Ocena jest wymagana")
    @Min(value = 0, message = "Ocena nie może być mniejsza niż 0")
    @Max(value = 10, message = "Ocena nie może być większa niż 10")
    private Integer score;

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public RatingDto() {
    }

    public RatingDto(Long id, Long userId, String username, Long bookId, String bookTitle, 
                     Integer score, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.score = score;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

}