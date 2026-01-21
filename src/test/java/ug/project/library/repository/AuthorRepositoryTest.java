package ug.project.library.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Reservation;
import ug.project.library.model.entity.User;
import ug.project.library.model.enumerate.ReservationStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    private Author sampleAuthor;

    @BeforeEach
    void setUp() {
        sampleAuthor = new Author("Adam", "Mickiewicz", new ArrayList<>());
    }

    @Test
    @DisplayName("Powinno zapisać autora w bazie danych")
    void save_ShouldPersistAuthor() {
        Author saved = authorRepository.save(sampleAuthor);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFullName()).isEqualTo("Adam Mickiewicz");
    }

    @Test
    @DisplayName("Powinno znaleźć autora po ID")
    void findById_ShouldReturnAuthor() {
        Author saved = authorRepository.save(sampleAuthor);
        Optional<Author> found = authorRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Adam");
    }

    @Test
    @DisplayName("Powinno zwrócić wszystkich autorów")
    void findAll_ShouldReturnList() {
        authorRepository.save(new Author("Henryk", "Sienkiewicz", new ArrayList<>()));
        authorRepository.save(new Author("Juliusz", "Słowacki", new ArrayList<>()));

        List<Author> authors = authorRepository.findAll();
        assertThat(authors.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Powinno zaktualizować dane autora")
    void update_ShouldChangeData() {
        Author saved = authorRepository.save(sampleAuthor);
        saved.setName("Aleksander");
        authorRepository.saveAndFlush(saved);

        Author updated = authorRepository.findById(saved.getId()).get();
        assertThat(updated.getName()).isEqualTo("Aleksander");
        assertThat(updated.getFullName()).isEqualTo("Aleksander Mickiewicz");
    }

    @Test
    @DisplayName("Powinno usunąć autora z bazy")
    void delete_ShouldRemoveAuthor() {
        Author saved = authorRepository.save(sampleAuthor);
        authorRepository.deleteById(saved.getId());

        Optional<Author> deleted = authorRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Powinno znaleźć autora po fragmencie imienia/nazwiska")
    void findByFullNameContaining_ShouldWork() {
        authorRepository.save(new Author("Stephen", "King", new ArrayList<>()));

        List<Author> results = authorRepository.findByFullNameContaining("Kin");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getSurname()).isEqualTo("King");
    }

    @Test
    @DisplayName("Powinno znaleźć autora po dokładnym imieniu i nazwisku")
    void findByNameAndSurname_ShouldReturnExactMatch() {
        authorRepository.save(sampleAuthor);

        Optional<Author> found = authorRepository.findByNameAndSurname("Adam", "Mickiewicz");
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Powinno zwrócić pusty Optional gdy autor nie istnieje")
    void findByNameAndSurname_ShouldReturnEmpty() {
        Optional<Author> found = authorRepository.findByNameAndSurname("Nieistniejący", "Autor");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Powinno sprawdzić czy autor istnieje po ID")
    void existsById_ShouldReturnTrue() {
        Author saved = authorRepository.save(sampleAuthor);
        boolean exists = authorRepository.existsById(saved.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Powinno zwrócić liczbę autorów")
    void count_ShouldReturnCorrectCount() {
        authorRepository.deleteAll();
        authorRepository.save(new Author("A", "B", new ArrayList<>()));
        authorRepository.save(new Author("C", "D", new ArrayList<>()));
        
        long count = authorRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Powinno zapisać wielu autorów naraz")
    void saveAll_ShouldPersistMultipleAuthors() {
        List<Author> authors = List.of(
            new Author("Author", "1", new ArrayList<>()),
            new Author("Author", "2", new ArrayList<>())
        );
        
        List<Author> saved = authorRepository.saveAll(authors);
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getId()).isNotNull();
        assertThat(saved.get(1).getId()).isNotNull();
    }

    @Test
    @DisplayName("Powinno usunąć wszystkich autorów")
    void deleteAll_ShouldClearDatabase() {
        authorRepository.save(sampleAuthor);
        authorRepository.deleteAll();
        
        assertThat(authorRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy autor istnieje po ID (negatywny)")
    void existsById_ShouldReturnFalseWhenNotExists() {
        boolean exists = authorRepository.existsById(999L);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Powinno pobrać autora używając JdbcTemplate i RowMapper")
    void customQueryWithRowMapper_ShouldMapCorrectly() {
        authorRepository.save(new Author("Stanisław", "Lem", new ArrayList<>()));

        RowMapper<Author> authorRowMapper = (rs, rowNum) -> {
            Author author = new Author();
            author.setId(rs.getLong("id"));
            author.setName(rs.getString("name"));
            author.setSurname(rs.getString("surname"));
            return author;
        };

        String sql = "SELECT id, name, surname FROM authors WHERE surname = ?";
        Author author = jdbcTemplate.queryForObject(sql, authorRowMapper, "Lem");

        assertThat(author).isNotNull();
        assertThat(author.getName()).isEqualTo("Stanisław");
    }

    @Test
    @DisplayName("Powinno wyrzucić wyjątek przy próbie zapisu duplikatu autora")
    void save_ShouldThrowException_WhenDuplicateNameAndSurname() {
        authorRepository.saveAndFlush(new Author("Adam", "Mickiewicz", new ArrayList<>()));
        
        Author duplicate = new Author("Adam", "Mickiewicz", new ArrayList<>());
        
        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            authorRepository.saveAndFlush(duplicate);
        });
    }

    @Test
    @DisplayName("Powinno obsłużyć paginację dla najpopularniejszych autorów")
    void findMostPopularAuthors_ShouldHandlePagination() {
        // Przygotuj autora, książkę, użytkownika i rezerwację
        Author author = authorRepository.save(new Author("Test", "Autor", new ArrayList<>()));
        Book book = new Book();
        book.setTitle("Testowa książka");
        book.setPublisher("Testowe wydawnictwo");
        book.setYearPublished(2020);
        book.setQuantityAvailable(5);
        book.setAuthors(List.of(author));
        book = bookRepository.save(book);

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setEncryptedPassword("password");
        user.setUserRole(ug.project.library.model.enumerate.UserRole.USER);
        user = userRepository.save(user);

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.WYPOŻYCZONA);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setDeadline(LocalDateTime.now().plusDays(7));
        reservationRepository.save(reservation);

        Page<Author> page = authorRepository.findMostPopularAuthors(PageRequest.of(0, 10));
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getName()).isEqualTo("Test");
    }
}