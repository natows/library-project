package ug.project.library.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import ug.project.library.model.entity.Rating;


@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(
        name = "book_author",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;


    @ManyToMany
    @JoinTable(
        name = "book_genre",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    @Column(name = "avgRating" )
    private Double avgRating;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Rating> ratings = new ArrayList<>();

    @Column(length = 4)
    private int yearPublished;

    @Column(nullable = false)
    private String publisher;// moze jako klasa?

    private String coverImageUrl;

    private int quantityAvailable;


    public Book() {
    }
    
    public Book(String title, List<Author> authors, List<Genre> genres, Double avgRating, int yearPublished, String publisher, String coverImageUrl, int quantityAvailable){
        this.title = title;
        this.authors = authors;
        this.genres = genres;
        this.avgRating = avgRating;
        this.yearPublished = yearPublished;
        this.publisher = publisher;
        this.coverImageUrl = coverImageUrl;
        this.quantityAvailable = quantityAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.avgRating = avgRating;
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
