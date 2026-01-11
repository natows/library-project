package ug.project.library.model.entity;

import jakarta.persistence.*;
import ug.project.library.model.enumerate.UserRole;
import jakarta.validation.constraints.Email; 
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String username;

    @Column(nullable = false)
    private String encryptedPassword;

    @Column(unique=true)
    @Email(message = "Niepoprawny adres email")
    @NotBlank(message = "Email nie może być pusty")
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User() {
    }

    public User(String username, String encryptedPassword, String email, UserRole userRole) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.email = email;

        this.userRole = userRole;
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

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole role) {
        this.userRole = role;
    }

}
