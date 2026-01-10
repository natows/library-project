package ug.project.library.model.entity;

import java.util.List;

import jakarta.persistence.*;


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

    //tez pewnie jakas tabela laczaca z userem
    public double rating;

    @Column(length = 4)
    private int yearPublished;

    @Column(nullable = false)
    private String publisher;

    private String coverImageUrl;

    private int quantityAvailable;


    
}
