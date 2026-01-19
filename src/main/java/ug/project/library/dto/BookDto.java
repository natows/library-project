package ug.project.library.dto;

import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Genre;

import java.util.List;
import jakarta.validation.constraints.*;

import ug.project.library.dto.AuthorDto;
import ug.project.library.dto.GenreDto;

public class BookDto {
    private Long id;
    @NotBlank(message = "Tytuł książki nie może być pusty")
    @Size(max = 200, message = "Tytuł książki nie może być dłuższy niż 200 znaków")
    private String title;

    @NotEmpty(message = "Lista autorów nie może być pusta")
    private List<AuthorDto> authors;

    @NotEmpty(message = "Lista gatunków nie może być pusta")
    private List<GenreDto> genres;

    @DecimalMin(value = "0.0", message = "Średnia ocena nie może być mniejsza niż 0")
    @DecimalMax(value = "10.0", message = "Średnia ocena nie może być większa niż 10")
    private Double avgRating;

    @Min(value = 1, message = "Rok wydania musi być większy od 0")
    private int yearPublished;

    @NotBlank(message = "Wydawnictwo nie może być puste")
    @Size(max = 100, message = "Nazwa wydawnictwa nie może być dłuższa niż 100 znaków")
    private String publisher;

    @Size(max = 500, message = "URL okładki nie może być dłuższy niż 500 znaków")
    private String coverImageUrl;

    @Min(value = 0, message = "Dostępna ilość egzemplarzy nie może być ujemna")
    private int quantityAvailable;


    public BookDto() {

    }

    public BookDto(Long id, String title, List<AuthorDto> authors, List<GenreDto> genres, Double avgRating, int yearPublished, String publisher, String coverImageUrl, int quantityAvailable) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.genres = genres;
        this.avgRating = avgRating;
        this.yearPublished = yearPublished;
        this.publisher = publisher;
        this.coverImageUrl = coverImageUrl;
        this.quantityAvailable = quantityAvailable;
    }
    public void setId(Long id){
        this.id=id;
    }
    public Long getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AuthorDto> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorDto> authors) {
        this.authors = authors;
    }

    public List<GenreDto> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreDto> genres) {
        this.genres = genres;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating; //jak bedziesz robic te flagi to tu ma byc przedzial 0 -10
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
}
