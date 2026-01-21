package ug.project.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ug.project.library.dto.AuthorDto;
import ug.project.library.dto.BookDto;
import ug.project.library.dto.GenreDto;
import ug.project.library.exceptions.BookAlreadyExistsException;
import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Genre;
import ug.project.library.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private BookService bookService;

    private BookDto bookDto;
    private Author author;

    @BeforeEach
    void setUp() {
        AuthorDto authorDto = new AuthorDto(1L, "J.K.", "Rowling");
        GenreDto genreDto = new GenreDto(1L, "Fantasy");
        
        bookDto = new BookDto();
        bookDto.setTitle("Harry Potter");
        bookDto.setAuthors(List.of(authorDto));
        bookDto.setGenres(List.of(genreDto));
        bookDto.setPublisher("Bloomsbury");
        bookDto.setYearPublished(1997);
        bookDto.setQuantityAvailable(10);

        author = new Author();
        author.setId(1L);
        author.setName("J.K.");
        author.setSurname("Rowling");
    }

    @Test
    @DisplayName("addBook should throw BookAlreadyExistsException when book already exists")
    void addBook_ShouldThrowException_WhenBookExists() {
        // Given
        when(authorService.findOrCreateAuthor(anyString(), anyString())).thenReturn(author);
        when(bookRepository.findByTitleAndAuthors(eq("Harry Potter"), anyList(), eq(1L)))
            .thenReturn(List.of(new Book()));

        // When & Then
        assertThrows(BookAlreadyExistsException.class, () -> bookService.addBook(bookDto));
        
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook should handle null authors and genres")
    void updateBook_ShouldHandleNullLists() {
        // Given
        Long bookId = 1L;
        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Old Title");
        existingBook.setAuthors(new ArrayList<>());
        existingBook.setGenres(new ArrayList<>());

        BookDto updateDto = new BookDto();
        updateDto.setTitle("New Title");
        updateDto.setAuthors(null); // Explicitly null
        updateDto.setGenres(null);  // Explicitly null
        updateDto.setAvgRating(null);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BookDto result = bookService.updateBook(bookId, updateDto);

        // Then
        assertNotNull(result);
        verify(bookRepository).save(existingBook);
        verify(authorService, never()).findOrCreateAuthor(anyString(), anyString());
    }

    @Test
    @DisplayName("addBook should save book when it does not exist")
    void addBook_ShouldSaveBook_WhenBookDoesNotExist() {
        // Given
        when(authorService.findOrCreateAuthor(anyString(), anyString())).thenReturn(author);
        when(genreService.findOrCreateGenre(anyString())).thenReturn(new Genre());
        when(bookRepository.findByTitleAndAuthors(eq("Harry Potter"), anyList(), eq(1L)))
            .thenReturn(new ArrayList<>());
        
        Book savedBook = new Book();
        savedBook.setId(100L);
        savedBook.setTitle("Harry Potter");
        savedBook.setAuthors(List.of(author));
        savedBook.setGenres(List.of(new Genre()));
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // When
        bookService.addBook(bookDto);

        // Then
        verify(bookRepository, times(1)).save(any(Book.class));
    }
}
