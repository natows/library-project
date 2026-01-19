package ug.project.library.dto;

import jakarta.validation.constraints.*;

public class AuthorDto {
    private Long id;

    @NotBlank(message = "Imię autora nie może być puste")
    @Size(max = 50, message = "Imię autora nie może być dłuższe niż 50 znaków")
    private String name;

    @NotBlank(message = "Nazwisko autora nie może być puste")
    @Size(max = 50, message = "Nazwisko autora nie może być dłuższe niż 50 znaków")
    private String surname;


    public AuthorDto() {
    }

    public AuthorDto(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public String getFullName() {
        return name +  " " + surname;
    }
}