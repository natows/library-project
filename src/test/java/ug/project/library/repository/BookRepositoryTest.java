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
    void setUp(){
        testAuthor = new Author();
        testAuthor.setName("J.K.");
        testAuthor.setSurname("Rowling");

        testGenre = new Genre();
        testGenre.setName("Fantasy");

        testBook = new Book();
        testBook.setTitle("Harry Potter");
        testBook.setAuthors(List.of(testAuthor));
        testBook.setGenres(List.of(testGenre));
        testBook.setRating(9.5);
        testBook.setYearPublished(1997);
        testBook.setPublisher("Bloomsbury");
        testBook.setCoverImageUrl("http://example.com/cover.jpg");
        testBook.setQuantityAvailable(5);


    }

    @Test
    void shouldSaveBook() {
        

        Book savedBook = bookRepository.save(testBook);
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Harry Potter");
        assertThat(savedBook.getRating()).isEqualTo(9.5);

    }
}