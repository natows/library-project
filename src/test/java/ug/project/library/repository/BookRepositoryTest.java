package ug.project.library.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Genre;
import ug.project.library.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Optional;
@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {
    private Book testBook;
    private Author testAuthor;
    private Genre testGenre;
    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        Author author = new Author();
        author.setName("J.K.");
        author.setSurname("Rowling");

        Genre fantasy = new Genre();
        fantasy.setName("Fantasy");

        Book book1 = new Book();
        book1.setTitle("Harry Potter");
        book1.setAuthors(List.of(author));
        book1.setGenres(List.of(fantasy));
        book1.setRating(9.5);
        book1.setYearPublished(1997);
        book1.setPublisher("Bloomsbury");
        book1.setQuantityAvailable(5);

        Book book2 = new Book();
        book2.setTitle("Fantastic Beasts");
        book2.setAuthors(List.of(author));
        book2.setGenres(List.of(fantasy));
        book2.setRating(8.0);
        book2.setYearPublished(2001);
        book2.setPublisher("Bloomsbury");
        book2.setQuantityAvailable(3);

        bookRepository.saveAll(List.of(book1, book2));
    }


    @Test
    void shouldSaveBook() {
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(2);
    }

    @Test
    void shouldFindBookById() {
        Book book = bookRepository.findAll().get(0);

        Optional<Book> found = bookRepository.findById(book.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Harry Potter");
    }

    @Test
    void shouldReturnEmptyWhenBookNotFound() {
        Optional<Book> book = bookRepository.findById(999L);

        assertThat(book).isEmpty();
    }

    @Test
    void shouldFindAllBooks() {
        List<Book> books = bookRepository.findAll();

        assertThat(books).hasSize(2);
    }

    @Test
    void shouldDeleteBook() {
        Book book = bookRepository.findAll().get(0);

        bookRepository.deleteById(book.getId());

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }

    @Test
    void shouldNotCrashWhenDeletingNonExistingBook() {
        bookRepository.deleteById(999L);

        assertThat(bookRepository.findAll()).hasSize(2);
    }

    @Test
    void shouldFindBooksByTitleContaining() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> result = bookRepository.findByTitleContaining("Harry", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Harry");
    }


    @Test
    void shouldFindBooksByPublisher() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> result = bookRepository.findByPublisher("Bloomsbury", pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldReturnTopRatedBooks() {
        Pageable pageable = PageRequest.of(0, 1);

        List<Book> books = bookRepository.findTopRatedBooks(pageable);

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getRating()).isEqualTo(9.5);
    }






}