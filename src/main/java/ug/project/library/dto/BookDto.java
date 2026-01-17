package ug.project.library.dto;

import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Genre;

import java.util.List;

public class BookDto {
    private Long id;
    private String title;
    private List<Author> authors;
    private List<Genre> genres;
    private Double avgRating;
    private int yearPublished;
    private String publisher;
    private String coverImageUrl;
    private int quantityAvailable;


    public BookDto() {

    }

    public BookDto(Long id, String title, List<Author> authors, List<Genre> genres, Double avgRating, int yearPublished, String publisher, String coverImageUrl, int quantityAvailable) {
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

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
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
