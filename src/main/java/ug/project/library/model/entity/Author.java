package ug.project.library.model.entity;

import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    @ManyToMany(mappedBy = "authors")
    private List<Book> books ;
    
    
}
