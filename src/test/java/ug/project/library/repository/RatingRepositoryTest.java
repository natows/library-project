package ug.project.library.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Rating;
import ug.project.library.model.entity.User;
import ug.project.library.model.enumerate.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User testUser;
    private Book testBook;
    private Rating testRating;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("user", "pass", "user@test.com", UserRole.USER);
        testUser = userRepository.save(testUser);

        testBook = new Book();
        testBook.setTitle("Title");
        testBook.setPublisher("Publisher");
        testBook = bookRepository.save(testBook);

        testRating = new Rating(testUser, testBook, 5);
    }

    @Test
    @DisplayName("Powinno zapisać ocenę")
    void save_ShouldPersistRating() {
        Rating saved = ratingRepository.save(testRating);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getScore()).isEqualTo(5);
    }

    @Test
    @DisplayName("Powinno znaleźć ocenę po ID")
    void findById_ShouldReturnRating() {
        Rating saved = ratingRepository.save(testRating);
        Optional<Rating> found = ratingRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Powinno zwrócić wszystkie oceny")
    void findAll_ShouldReturnList() {
        ratingRepository.save(testRating);
        List<Rating> all = ratingRepository.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    @DisplayName("Powinno zaktualizować wynik oceny")
    void update_ShouldChangeScore() {
        Rating saved = ratingRepository.save(testRating);
        saved.setScore(4);
        ratingRepository.saveAndFlush(saved);

        Rating updated = ratingRepository.findById(saved.getId()).get();
        assertThat(updated.getScore()).isEqualTo(4);
    }

    @Test
    @DisplayName("Powinno usunąć ocenę")
    void delete_ShouldRemoveRating() {
        Rating saved = ratingRepository.save(testRating);
        ratingRepository.deleteById(saved.getId());
        assertThat(ratingRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Powinno sprawdzić czy ocena istnieje")
    void existsById_ShouldReturnTrue() {
        Rating saved = ratingRepository.save(testRating);
        assertThat(ratingRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("Powinno zwrócić liczbę ocen")
    void count_ShouldReturnCorrectCount() {
        ratingRepository.save(testRating);
        assertThat(ratingRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Powinno zapisać wiele ocen")
    void saveAll_ShouldPersistMultiple() {
        User u2 = userRepository.save(new User("u2", "p", "u2@test.com", UserRole.USER));
        Rating r2 = new Rating(u2, testBook, 3);
        List<Rating> saved = ratingRepository.saveAll(List.of(testRating, r2));
        assertThat(saved).hasSize(2);
    }

    @Test
    @DisplayName("Powinno usunąć wszystkie oceny")
    void deleteAll_ShouldClearDatabase() {
        ratingRepository.save(testRating);
        ratingRepository.deleteAll();
        assertThat(ratingRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy ocena istnieje (negatywny)")
    void existsById_ShouldReturnFalse() {
        assertThat(ratingRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("Powinno pobrać ocenę przez JdbcTemplate i RowMapper")
    void customQueryWithRowMapper_ShouldWork() {
        Rating saved = ratingRepository.save(testRating);
        RowMapper<Rating> mapper = (rs, rowNum) -> {
            Rating r = new Rating();
            r.setId(rs.getLong("id"));
            r.setScore(rs.getInt("score"));
            return r;
        };
        Rating r = jdbcTemplate.queryForObject("SELECT id, score FROM ratings WHERE id = ?", mapper, saved.getId());
        assertThat(r).isNotNull();
        assertThat(r.getScore()).isEqualTo(5);
    }

    @Test
    @DisplayName("Powinno obliczyć średnią ocen dla książki")
    void calculateAverageRating_ShouldWork() {
        ratingRepository.save(new Rating(testUser, testBook, 5));
        User u2 = userRepository.save(new User("u2", "p", "u2@test.com", UserRole.USER));
        ratingRepository.save(new Rating(u2, testBook, 3));
        
        Double avg = ratingRepository.calculateAverageRating(testBook.getId());
        assertThat(avg).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Powinno znaleźć ostatnie oceny użytkownika")
    void findRecentRatingsByUser_ShouldWork() {
        ratingRepository.save(testRating);
        List<Rating> recent = ratingRepository.findRecentRatingsByUser(testUser.getId(), LocalDateTime.now().minusDays(1));
        assertThat(recent).isNotEmpty();
    }
}
