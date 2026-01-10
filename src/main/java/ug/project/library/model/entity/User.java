package ug.project.library.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String username;

    private String encryotedPassword;

    @Column(unique=true)
    private String email;


    
}
