package ug.project.library.model.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name= "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String name;

    @ManyToMany(mappedBy ="genres")
    private List<Book> books;
    
}
