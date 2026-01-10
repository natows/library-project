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

    public User() {
    }

    public User(String username, String encryotedPassword, String email) {
        this.username = username;
        this.encryotedPassword = encryotedPassword;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryotedPassword() {
        return encryotedPassword;
    }

    public void setEncryotedPassword(String encryotedPassword) {
        this.encryotedPassword = encryotedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
