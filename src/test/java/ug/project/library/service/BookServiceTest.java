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
import ug.project.library.exceptions.BookNotAvailableException;
import ug.project.library.exceptions.BookNotFoundException;
import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Genre;
import ug.project.library.repository.BookRepository;
import ug.project.library.dao.BookDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookDao bookDao;

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
        when(authorService.findOrCreateAuthor(anyString(), anyString())).thenReturn(author);
        when(bookRepository.findByTitleAndAuthors(eq("Harry Potter"), anyList(), eq(1L)))
            .thenReturn(List.of(new Book()));

        assertThrows(BookAlreadyExistsException.class, () -> bookService.addBook(bookDto));
        
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook should handle null authors and genres")
    void updateBook_ShouldHandleNullLists() {
        Long bookId = 1L;
        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Old Title");
        existingBook.setAuthors(new ArrayList<>());
        existingBook.setGenres(new ArrayList<>());

        BookDto updateDto = new BookDto();
        updateDto.setTitle("New Title");
        updateDto.setAuthors(null); 
        updateDto.setGenres(null);  
        updateDto.setAvgRating(null);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));


        BookDto result = bookService.updateBook(bookId, updateDto);

        assertNotNull(result);
        verify(bookRepository).save(existingBook);
        verify(authorService, never()).findOrCreateAuthor(anyString(), anyString());
    }

    @Test
    @DisplayName("addBook should save book when it does not exist")
    void addBook_ShouldSaveBook_WhenBookDoesNotExist() {

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

        bookService.addBook(bookDto);

        verify(authorService, atLeastOnce()).findOrCreateAuthor(anyString(), anyString());
        verify(genreService, atLeastOnce()).findOrCreateGenre(anyString());
        verify(bookRepository).findByTitleAndAuthors(eq("Harry Potter"), anyList(), eq(1L));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("getBookById should return book when it exists")
    void getBookById_ShouldReturnBook_WhenExists() {
        Long id = 1L;
        Book book = new Book();
        book.setId(id);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(id);

        assertEquals(id, result.getId());
        verify(bookRepository).findById(id);
    }

    @Test
    @DisplayName("getBookById should throw BookNotFoundException when book does not exist")
    void getBookById_ShouldThrowException_WhenDoesNotExist() {
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(id));
        verify(bookRepository).findById(id);
    }

    @Test
    @DisplayName("getBookDtoById should return book dto when book exists")
    void getBookDtoById_ShouldReturnDto_WhenExists() {
        Long id = 1L;
        Book book = new Book();
        book.setId(id);
        book.setTitle("Title");
        book.setAuthors(new ArrayList<>());
        book.setGenres(new ArrayList<>());
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        BookDto result = bookService.getBookDtoById(id);

        assertEquals("Title", result.getTitle());
        verify(bookRepository).findById(id);
    }

    @Test
    @DisplayName("getAllBooksDto should return page of book dtos")
    void getAllBooksDto_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = new Book();
        book.setAuthors(new ArrayList<>());
        book.setGenres(new ArrayList<>());
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<BookDto> result = bookService.getAllBooksDto(pageable);

        assertEquals(1, result.getContent().size());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getTopRatedBooks should return list of book dtos")
    void getTopRatedBooks_ShouldReturnList() {
        int limit = 5;
        Book book = new Book();
        book.setAuthors(new ArrayList<>());
        book.setGenres(new ArrayList<>());
        when(bookRepository.findTopRatedBooks(any(PageRequest.class))).thenReturn(List.of(book));

        List<BookDto> result = bookService.getTopRatedBooks(limit);

        assertEquals(1, result.size());
        verify(bookRepository).findTopRatedBooks(argThat(pr -> pr.getPageSize() == limit));
    }

    @Test
    @DisplayName("deleteBook should delete book when it exists")
    void deleteBook_ShouldDelete_WhenExists() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);

        bookService.deleteBook(id);

        verify(bookRepository).existsById(id);
        verify(bookRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteBook should throw BookNotFoundException when book does not exist")
    void deleteBook_ShouldThrowException_WhenDoesNotExist() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(id));
        verify(bookRepository).existsById(id);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("deincrementQuantityAvailable should call dao when available")
    void deincrementQuantityAvailable_ShouldCallDao_WhenAvailable() {
        Book book = new Book();
        book.setId(1L);
        book.setQuantityAvailable(5);

        bookService.deincrementQuantityAvailable(book);

        verify(bookDao).deincrementQuantityAvailable(1L);
    }

    @Test
    @DisplayName("deincrementQuantityAvailable should throw exception when not available")
    void deincrementQuantityAvailable_ShouldThrowException_WhenNotAvailable() {
        Book book = new Book();
        book.setTitle("Empty Book");
        book.setId(1L);
        book.setQuantityAvailable(0);

        assertThrows(BookNotAvailableException.class, () -> bookService.deincrementQuantityAvailable(book));
        verifyNoInteractions(bookDao);
    }

    @Test
    @DisplayName("incrementQuantityAvailable should call dao")
    void incrementQuantityAvailable_ShouldCallDao() {
        Book book = new Book();
        book.setId(1L);

        bookService.incrementQuantityAvailable(book);

        verify(bookDao).incrementBookQuantity(1L);
    }

    @Test
    @DisplayName("searchBooks should return page of results")
    void searchBooks_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = new Book();
        book.setAuthors(new ArrayList<>());
        book.setGenres(new ArrayList<>());
        when(bookRepository.searchBooks(any(), any(), any(), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(book)));

        Page<BookDto> result = bookService.searchBooks("title", "author", "keyword", pageable);

        assertEquals(1, result.getContent().size());
        verify(bookRepository).searchBooks("title", "author", "keyword", pageable);
    }

    @Test
    @DisplayName("searchBooks should handle blank strings as null")
    void searchBooks_ShouldHandleBlanks() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.searchBooks(null, null, null, pageable))
            .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookService.searchBooks(" ", "", "  ", pageable);

        verify(bookRepository).searchBooks(null, null, null, pageable);
    }
    @Test
    @DisplayName("updateBook should update all fields and save")
    void updateBook_ShouldUpdateAllFields() {
        Long bookId = 1L;
        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setAuthors(new ArrayList<>());
        existingBook.setGenres(new ArrayList<>());

        AuthorDto authorDto = new AuthorDto(2L, "New", "Author");
        GenreDto genreDto = new GenreDto(2L, "New Genre");
        
        BookDto updateDto = new BookDto();
        updateDto.setTitle("Updated Title");
        updateDto.setAuthors(List.of(authorDto));
        updateDto.setGenres(List.of(genreDto));
        updateDto.setAvgRating(4.5);
        updateDto.setYearPublished(2020);
        updateDto.setPublisher("New Publisher");
        updateDto.setCoverImageUrl("http://image.com");
        updateDto.setQuantityAvailable(20);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(authorService.findOrCreateAuthor("New", "Author")).thenReturn(new Author());
        when(genreService.findOrCreateGenre("New Genre")).thenReturn(new Genre());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookDto result = bookService.updateBook(bookId, updateDto);

        assertEquals("Updated Title", existingBook.getTitle());
        assertEquals(4.5, existingBook.getAvgRating());
        assertEquals(2020, existingBook.getYearPublished());
        assertEquals("New Publisher", existingBook.getPublisher());
        assertEquals("http://image.com", existingBook.getCoverImageUrl());
        assertEquals(20, existingBook.getQuantityAvailable());
        verify(bookRepository).findById(bookId);
        verify(authorService).findOrCreateAuthor("New", "Author");
        verify(genreService).findOrCreateGenre("New Genre");
        verify(bookRepository).save(existingBook);
    }

    @Test
    @DisplayName("searchBooks should call repository with correct parameters")
    void searchBooks_ShouldCallRepositoryWithParams() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.searchBooks("title", "author", "keyword", pageable))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookService.searchBooks("title", "author", "keyword", pageable);

        verify(bookRepository).searchBooks("title", "author", "keyword", pageable);
    }

    @Test
    @DisplayName("addBook should handle null avgRating and map correctly")
    void addBook_ShouldHandleNullAvgRating() {
        bookDto.setAvgRating(null);
        when(authorService.findOrCreateAuthor(anyString(), anyString())).thenReturn(author);
        when(genreService.findOrCreateGenre(anyString())).thenReturn(new Genre());
        when(bookRepository.findByTitleAndAuthors(anyString(), anyList(), anyLong()))
                .thenReturn(new ArrayList<>());
        
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle(bookDto.getTitle());
        savedBook.setAuthors(new ArrayList<>());
        savedBook.setGenres(new ArrayList<>());
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        bookService.addBook(bookDto);

        verify(bookRepository).save(argThat(book -> book.getAvgRating() == null));
    }

    @Test
    @DisplayName("addBook should handle null genres and map to empty list")
    void addBook_ShouldHandleNullGenres() {
        bookDto.setGenres(null);
        when(authorService.findOrCreateAuthor(anyString(), anyString())).thenReturn(author);
        when(bookRepository.findByTitleAndAuthors(anyString(), anyList(), anyLong()))
                .thenReturn(new ArrayList<>());
        
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle(bookDto.getTitle());
        savedBook.setAuthors(new ArrayList<>());
        savedBook.setGenres(new ArrayList<>());
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        bookService.addBook(bookDto);

        verify(genreService, never()).findOrCreateGenre(anyString());
        verify(bookRepository).save(argThat(book -> book.getGenres() != null && book.getGenres().isEmpty()));
    }
    @Test
    @DisplayName("updateBook should update avgRating when not null")
    void updateBook_ShouldUpdateAvgRating_WhenNotNull() {
        Long bookId = 1L;
        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setAvgRating(3.0);
        existingBook.setAuthors(new ArrayList<>());
        existingBook.setGenres(new ArrayList<>());

        BookDto updateDto = new BookDto();
        updateDto.setAvgRating(4.5);
        updateDto.setAuthors(new ArrayList<>());
        updateDto.setGenres(new ArrayList<>());

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookService.updateBook(bookId, updateDto);

        assertEquals(4.5, existingBook.getAvgRating());
        verify(bookRepository).save(existingBook);
    }

    @Test
    @DisplayName("searchBooks should handle mixed null, blank and valid parameters")
    void searchBooks_ShouldHandleMixedParameters() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.searchBooks(eq("Title"), eq(null), eq(null), eq(pageable)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookService.searchBooks("Title", " ", null, pageable);

        verify(bookRepository).searchBooks("Title", null, null, pageable);
    }

    @Test
    @DisplayName("searchBooks should handle keyword correctly")
    void searchBooks_ShouldHandleKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.searchBooks(null, null, "Java", pageable))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookService.searchBooks(null, "", "Java", pageable);

        verify(bookRepository).searchBooks(null, null, "Java", pageable);
    }

    @Test
    @DisplayName("addBook should set avgRating when provided in DTO")
    void addBook_ShouldSetAvgRating_WhenProvided() {
        bookDto.setAvgRating(4.0);
        when(authorService.findOrCreateAuthor(anyString(), anyString())).thenReturn(author);
        when(genreService.findOrCreateGenre(anyString())).thenReturn(new Genre());
        when(bookRepository.findByTitleAndAuthors(anyString(), anyList(), anyLong()))
                .thenReturn(new ArrayList<>());
        
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle(bookDto.getTitle());
        savedBook.setAuthors(new ArrayList<>());
        savedBook.setGenres(new ArrayList<>());
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        bookService.addBook(bookDto);

        verify(bookRepository).save(argThat(book -> book.getAvgRating().equals(4.0)));
    }
}
