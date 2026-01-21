package ug.project.library.service;

import org.springframework.stereotype.Service;

import ug.project.library.dto.AuthorDto;
import ug.project.library.exceptions.AuthorNotFoundException;
import ug.project.library.exceptions.BookNotFoundException;
import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Author;
import ug.project.library.repository.AuthorRepository;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }


    @Transactional(readOnly = true)
    public Page<AuthorDto> getAllAuthorDto(Pageable pageable){
        Page<Author> authors = authorRepository.findAll(pageable);
        Page<AuthorDto> authorsDto = authors.map(this::mapAuthorToDto);
        return authorsDto;


    }


    private Author mapDtoToAuthor(AuthorDto authorDto){
        return new Author(
            authorDto.getName(),
            authorDto.getSurname(),
            new ArrayList<>()
        );

    }

    private AuthorDto mapAuthorToDto(Author author){
        return new AuthorDto(
            author.getId(),
            author.getName(),
            author.getSurname()
        );

    }
    


    @Transactional
    public Author findOrCreateAuthor(String name, String surname) {
        return authorRepository
            .findByNameAndSurname(name, surname)
            .orElseGet(() -> {
                Author author = new Author();
                author.setName(name);
                author.setSurname(surname);
                return authorRepository.save(author);
            });
    }


    @Transactional(readOnly = true)
    public AuthorDto getAuthorDtoById(Long id){
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException(id));
        return mapAuthorToDto(author);
    }

    @Transactional
    public AuthorDto addAuthor(AuthorDto authorDto){
        Author author  = mapDtoToAuthor(authorDto);
        Author savedAuthor = authorRepository.save(author);
        return mapAuthorToDto(savedAuthor);
    }

    @Transactional
    public AuthorDto updateAuthor(Long id, AuthorDto authorDto){
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException(id));

        author.setName(authorDto.getName());
        author.setSurname(authorDto.getSurname());
        return mapAuthorToDto(author);
    }

    @Transactional
    public void deleteAuthor(Long id){
        if (!authorRepository.existsById(id)) {
            throw new AuthorNotFoundException(id);
        }
        authorRepository.deleteById(id);
        
    }



}
