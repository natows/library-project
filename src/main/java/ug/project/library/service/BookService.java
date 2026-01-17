package ug.project.library.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ug.project.library.dto.BookDto;
import ug.project.library.exceptions.*;
import ug.project.library.model.entity.Book;
import ug.project.library.repository.BookRepository;
import java.util.stream.Collectors;
import ug.project.library.dao.*;


@Service

public class BookService {

    private final BookRepository bookRepository;
    private final BookDao bookDao;

    public BookService(BookRepository bookRepository, BookDao bookDao) {
        this.bookRepository = bookRepository;
        this.bookDao = bookDao;
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
    public BookDto addBook(BookDto bookDto){
        Book book = mapDtoToBook(bookDto);
        Book savedBook = bookRepository.save(book);
        BookDto convertedBook = mapBookToDto(savedBook);
        return convertedBook;


    }

    @Transactional
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = getBookById(id);
        
        book.setTitle(bookDto.getTitle());
        book.setAuthors(bookDto.getAuthors());
        book.setGenres(bookDto.getGenres());
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
        Book book = new Book(bookDto.getTitle(), bookDto.getAuthors(), bookDto.getGenres(), bookDto.getAvgRating(), bookDto.getYearPublished() , bookDto.getPublisher(), bookDto.getCoverImageUrl(), bookDto.getQuantityAvailable() );
        return book;
    }

    private BookDto mapBookToDto(Book book) {
        return new BookDto(book.getId(), book.getTitle(),
            book.getAuthors(),
            book.getGenres(),
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

   

    

}