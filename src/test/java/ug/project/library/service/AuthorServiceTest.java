package ug.project.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ug.project.library.dto.AuthorDto;
import ug.project.library.exceptions.AuthorNotFoundException;
import ug.project.library.model.entity.Author;
import ug.project.library.repository.AuthorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author;
    private AuthorDto authorDto;

    @BeforeEach
    void setUp() {
        author = new Author("Adam", "Mickiewicz", new ArrayList<>());
        author.setId(1L);

        authorDto = new AuthorDto(1L, "Adam", "Mickiewicz");
    }

    @Test
    @DisplayName("getAllAuthorDto should return page of DTOs")
    void getAllAuthorDto_ShouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Author> authorPage = new PageImpl<>(List.of(author));
        when(authorRepository.findAll(pageable)).thenReturn(authorPage);

        Page<AuthorDto> result = authorService.getAllAuthorDto(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Adam");
        verify(authorRepository).findAll(pageable);
    }

    @Test
    @DisplayName("findOrCreateAuthor should return existing author")
    void findOrCreateAuthor_ShouldReturnExistingAuthor() {
        when(authorRepository.findByNameAndSurname("Adam", "Mickiewicz")).thenReturn(Optional.of(author));

        Author result = authorService.findOrCreateAuthor("Adam", "Mickiewicz");

        assertThat(result).isEqualTo(author);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    @DisplayName("findOrCreateAuthor should create new author when not found")
    void findOrCreateAuthor_ShouldCreateNewAuthor() {
        when(authorRepository.findByNameAndSurname("New", "Author")).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Author result = authorService.findOrCreateAuthor("New", "Author");

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getSurname()).isEqualTo("Author");
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    @DisplayName("getAuthorDtoById should return DTO")
    void getAuthorDtoById_ShouldReturnDto() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorDto result = authorService.getAuthorDtoById(1L);

        assertThat(result.getName()).isEqualTo("Adam");
        verify(authorRepository).findById(1L);
    }

    @Test
    @DisplayName("getAuthorDtoById should throw exception when not found")
    void getAuthorDtoById_ShouldThrowException_WhenNotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.getAuthorDtoById(1L));
    }

    @Test
    @DisplayName("addAuthor should save and return DTO")
    void addAuthor_ShouldSaveAndReturnDto() {
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorDto result = authorService.addAuthor(authorDto);

        assertThat(result.getName()).isEqualTo("Adam");
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    @DisplayName("updateAuthor should update and return DTO")
    void updateAuthor_ShouldUpdateAndReturnDto() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorDto updateDto = new AuthorDto(1L, "Aleksander", "Mickiewicz");
        AuthorDto result = authorService.updateAuthor(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Aleksander");
        assertThat(author.getName()).isEqualTo("Aleksander");
    }

    @Test
    @DisplayName("deleteAuthor should delete when exists")
    void deleteAuthor_ShouldDelete_WhenExists() {
        when(authorRepository.existsById(1L)).thenReturn(true);

        authorService.deleteAuthor(1L);

        verify(authorRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteAuthor should throw exception when not exists")
    void deleteAuthor_ShouldThrowException_WhenNotExists() {
        when(authorRepository.existsById(1L)).thenReturn(false);

        assertThrows(AuthorNotFoundException.class, () -> authorService.deleteAuthor(1L));
        verify(authorRepository, never()).deleteById(anyLong());
    }
}
