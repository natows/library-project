package ug.project.library.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;

import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Genre;
import ug.project.library.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        bookRepository.deleteAll();

        Author author = new Author();
        author.setName("J.K.");
        author.setSurname("Rowling");
        testAuthor = authorRepository.save(author);

        Genre fantasy = new Genre();
        fantasy.setName("Fantasy");
        testGenre = genreRepository.save(fantasy);

        Book book1 = new Book();
        book1.setTitle("Harry Potter");
        book1.setAuthors(new ArrayList<>(List.of(testAuthor)));
        book1.setGenres(new ArrayList<>(List.of(testGenre)));
        book1.setAvgRating(9.5);
        book1.setYearPublished(1997);
        book1.setPublisher("Bloomsbury");
        book1.setQuantityAvailable(5);

        Book book2 = new Book();
        book2.setTitle("Fantastic Beasts");
        book2.setAuthors(new ArrayList<>(List.of(testAuthor)));
        book2.setGenres(new ArrayList<>(List.of(testGenre)));
        book2.setAvgRating(8.0);
        book2.setYearPublished(2001);
        book2.setPublisher("Bloomsbury");
        book2.setQuantityAvailable(3);

        bookRepository.saveAll(List.of(book1, book2));
    }


    @Test
    @DisplayName("Powinno zapisać książkę w bazie danych")
    void save_ShouldPersistBook() {
        Book book = new Book();
        book.setTitle("The Hobbit");
        book.setPublisher("George Allen & Unwin");
        book.setYearPublished(1937);
        book.setQuantityAvailable(10);
        
        Book saved = bookRepository.save(book);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("The Hobbit");
    }

    @Test
    @DisplayName("Powinno znaleźć książkę po ID")
    void findById_ShouldReturnBook() {
        Book book = bookRepository.findAll().get(0);
        Optional<Book> found = bookRepository.findById(book.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("Powinno zwrócić wszystkie książki")
    void findAll_ShouldReturnList() {
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Powinno zaktualizować dane książki")
    void update_ShouldChangeData() {
        Book book = bookRepository.findAll().get(0);
        book.setTitle("Updated Title");
        bookRepository.saveAndFlush(book);

        Book updated = bookRepository.findById(book.getId()).get();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    @DisplayName("Powinno usunąć książkę z bazy")
    void delete_ShouldRemoveBook() {
        Book book = bookRepository.findAll().get(0);
        bookRepository.deleteById(book.getId());

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }

    @Test
    @DisplayName("Powinno sprawdzić czy książka istnieje po ID")
    void existsById_ShouldReturnTrue() {
        Book book = bookRepository.findAll().get(0);
        assertThat(bookRepository.existsById(book.getId())).isTrue();
    }

    @Test
    @DisplayName("Powinno zwrócić liczbę książek")
    void count_ShouldReturnCorrectCount() {
        long count = bookRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Powinno zapisać wiele książek naraz")
    void saveAll_ShouldPersistMultipleBooks() {
        Book b1 = new Book(); b1.setTitle("B1"); b1.setPublisher("P1");
        Book b2 = new Book(); b2.setTitle("B2"); b2.setPublisher("P2");
        List<Book> saved = bookRepository.saveAll(List.of(b1, b2));
        assertThat(saved).hasSize(2);
    }

    @Test
    @DisplayName("Powinno usunąć wszystkie książki")
    void deleteAll_ShouldClearDatabase() {
        bookRepository.deleteAll();
        assertThat(bookRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy książka istnieje po ID (negatywny)")
    void existsById_ShouldReturnFalseWhenNotExists() {
        assertThat(bookRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("Powinno znaleźć książkę po tytule i autorach")
    void findByTitleAndAuthors_ShouldReturnMatchingBook() {
        Book book = bookRepository.findAll().get(0);
        List<Long> authorIds = book.getAuthors().stream().map(Author::getId).toList();
        
        List<Book> found = bookRepository.findByTitleAndAuthors(book.getTitle(), authorIds, (long) authorIds.size());
        
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getId()).isEqualTo(book.getId());
    }

    @Test
    @DisplayName("Nie powinno znaleźć książki gdy lista autorów się nie zgadza")
    void findByTitleAndAuthors_ShouldReturnEmptyWhenAuthorsDoNotMatch() {
        Book book = bookRepository.findAll().get(0);
        
        // Dodaj nowego autora, który nie jest autorem tej książki
        Author otherAuthor = new Author();
        otherAuthor.setName("Other");
        otherAuthor.setSurname("Author");
        otherAuthor = authorRepository.save(otherAuthor);
        
        List<Long> authorIds = new ArrayList<>(book.getAuthors().stream().map(Author::getId).toList());
        authorIds.add(otherAuthor.getId());
        
        List<Book> found = bookRepository.findByTitleAndAuthors(book.getTitle(), authorIds, (long) authorIds.size());
        
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Nie powinno znaleźć książki gdy brakuje jednego z autorów")
    void findByTitleAndAuthors_ShouldReturnEmptyWhenAuthorIsMissing() {
        // Stwórz książkę z dwoma autorami
        Author a1 = new Author("Author", "One", new ArrayList<>());
        Author a2 = new Author("Author", "Two", new ArrayList<>());
        a1 = authorRepository.save(a1);
        a2 = authorRepository.save(a2);
        
        Book book = new Book();
        book.setTitle("Two Authors Book");
        book.setAuthors(new ArrayList<>(List.of(a1, a2)));
        book.setPublisher("Test");
        book.setYearPublished(2020);
        book.setQuantityAvailable(5);
        bookRepository.save(book);
        
        // Szukaj tylko po jednym autorze
        List<Book> found = bookRepository.findByTitleAndAuthors("Two Authors Book", List.of(a1.getId()), 1L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Powinno pobrać książkę używając JdbcTemplate i RowMapper")
    void customQueryWithRowMapper_ShouldMapCorrectly() {
        RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));
            return book;
        };

        String sql = "SELECT id, title FROM books WHERE title = ?";
        Book book = jdbcTemplate.queryForObject(sql, bookRowMapper, "Harry Potter");

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo("Harry Potter");
    }

    @Test
    @DisplayName("Powinno znaleźć książki po tytule")
    void findByTitleContaining_ShouldWork() {
        Page<Book> result = bookRepository.findByTitleContaining("Harry", PageRequest.of(0, 10));
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).contains("Harry");
    }

    @Test
    @DisplayName("Powinno znaleźć książki po wydawcy")
    void findByPublisher_ShouldWork() {
        Page<Book> result = bookRepository.findByPublisher("Bloomsbury", PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Powinno zwrócić najlepiej oceniane książki")
    void findTopRatedBooks_ShouldWork() {
        List<Book> books = bookRepository.findTopRatedBooks(PageRequest.of(0, 1));
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getAvgRating()).isEqualTo(9.5);
    }

    @Test
    @DisplayName("Powinno wyszukać książki po wielu kryteriach")
    void searchBooks_ShouldWork() {
        Page<Book> result = bookRepository.searchBooks("Harry", "Rowling", "Fantasy", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Harry Potter");
    }






}