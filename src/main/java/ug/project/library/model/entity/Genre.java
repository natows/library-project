package ug.project.library.model.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name= "genres", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String name;

    @ManyToMany(mappedBy ="genres")
    private List<Book> books;

    public Genre() {
    }

    public Genre(String name, List<Book> books) {
        this.name = name;
        this.books = books;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
