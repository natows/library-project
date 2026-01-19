package ug.project.library.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ug.project.library.dto.AuthorDto;
import ug.project.library.dto.GenreDto;
import ug.project.library.dto.BookDto;
import ug.project.library.exceptions.*;
import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Genre;
import ug.project.library.repository.AuthorRepository;
import ug.project.library.repository.BookRepository;
import ug.project.library.repository.GenreRepository;

import java.util.stream.Collectors;
import ug.project.library.dao.*;


@Service

public class BookService {

    private final BookRepository bookRepository;
    private final BookDao bookDao;
    private final AuthorService authorService;
    private final GenreService genreService;

    public BookService(BookRepository bookRepository, BookDao bookDao, AuthorService authorService, GenreService genreService) {
        this.bookRepository = bookRepository;
        this.bookDao = bookDao;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long id){
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }



    @Transactional(readOnly = true)
    public BookDto getBookDtoById(Long id){
        Book book = getBookById(id);
        return mapBookToDto(book);
    }

    @Transactional(readOnly = true)
    public Page<BookDto> getAllBooksDto(Pageable pageable){
        Page<Book> books = bookRepository.findAll(pageable);
        Page<BookDto> booksDto = books.map(this::mapBookToDto);
        return booksDto;

    }

    @Transactional(readOnly = true)
    public List<BookDto> getTopRatedBooks(int limit){
        List<Book> books = bookRepository.findTopRatedBooks(PageRequest.of(0, limit));
        return books.stream().map(book -> mapBookToDto(book)).collect(Collectors.toList());
    }

    @Transactional
    public void checkIfBookExists(BookDto bookDto) {
        List<Author> authors = bookDto.getAuthors().stream()
                .map(a -> authorService.findOrCreateAuthor(a.getName(), a.getSurname()))
                .toList();

        List<Long> authorIds = authors.stream().map(Author::getId).toList();

        List<Book> existingBooks = bookRepository.findByTitleAndAuthors(bookDto.getTitle(), authorIds, (long) authorIds.size());

        if (!existingBooks.isEmpty()) {
            throw new BookAlreadyExistsException(bookDto.getTitle());
        }
    }
    @Transactional
    public BookDto addBook(BookDto bookDto){
        checkIfBookExists(bookDto);

        Book book = mapDtoToBook(bookDto);
        
        Book savedBook = bookRepository.save(book);
        BookDto convertedBook = mapBookToDto(savedBook);
        return convertedBook;
    }

    



    @Transactional
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = getBookById(id);

        List<Author> authors = bookDto.getAuthors().stream()
        .map(a -> authorService.findOrCreateAuthor(a.getName(), a.getSurname()))
        .toList();
    
        List<Genre> genres = bookDto.getGenres().stream()
            .map(g -> genreService.findOrCreateGenre(g.getName()))
            .toList();
        
        book.setTitle(bookDto.getTitle());
        book.setAuthors(authors);
        book.setGenres(genres);
        book.setAvgRating(bookDto.getAvgRating());
        book.setYearPublished(bookDto.getYearPublished());
        book.setPublisher(bookDto.getPublisher());
        book.setCoverImageUrl(bookDto.getCoverImageUrl());
        book.setQuantityAvailable(bookDto.getQuantityAvailable());
        
        Book updatedBook = bookRepository.save(book);
        return mapBookToDto(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id){
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }


    private Book mapDtoToBook(BookDto bookDto){
        List<Author> authors = bookDto.getAuthors().stream()
        .map(a -> authorService.findOrCreateAuthor(a.getName(), a.getSurname()))
        .toList();
        List<Genre> genres = bookDto.getGenres().stream()
        .map(a -> genreService.findOrCreateGenre(a.getName()))
        .toList();

        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthors(authors);
        book.setGenres(genres);
        book.setAvgRating(bookDto.getAvgRating()); 
        book.setYearPublished(bookDto.getYearPublished());
        book.setPublisher(bookDto.getPublisher());
        book.setCoverImageUrl(bookDto.getCoverImageUrl());
        book.setQuantityAvailable(bookDto.getQuantityAvailable());
        return book;
     }

    private BookDto mapBookToDto(Book book) {
        return new BookDto(
            book.getId(),
            book.getTitle(),
            book.getAuthors().stream()
                .map(a -> new AuthorDto(a.getId(), a.getName(), a.getSurname()))
                .toList(),
            book.getGenres().stream()
                .map(g -> new GenreDto(g.getId(), g.getName()))
                .toList(),
            book.getAvgRating(),
            book.getYearPublished(),
            book.getPublisher(),
            book.getCoverImageUrl(),
            book.getQuantityAvailable()
        );
    }

    private void validateBookAvailable(Book book ){
        if (book.getQuantityAvailable() <= 0) {
            throw new BookNotAvailableException(book.getTitle(), book.getId());
        }
    }
    

    @Transactional
    public void deincrementQuantityAvailable(Book book){
        validateBookAvailable(book);
        bookDao.deincrementQuantityAvailable(book.getId());
    }

    @Transactional
    public void incrementQuantityAvailable(Book book){
        bookDao.incrementBookQuantity(book.getId());
    }

    @Transactional(readOnly = true)
    public Page<BookDto> searchBooks(String title, String author, String keyword, Pageable pageable) {
        Page<Book> books = bookRepository.searchBooks(
            title != null && !title.isBlank() ? title : null,
            author != null && !author.isBlank() ? author : null,
            keyword != null && !keyword.isBlank() ? keyword : null,
            pageable
        );
        return books.map(this::mapBookToDto);
    }



   

    

}