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
import ug.project.library.model.entity.User;
import ug.project.library.model.enumerate.UserRole;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        sampleUser = new User("testuser", "password", "test@example.com", UserRole.USER);
    }

    @Test
    @DisplayName("Powinno zapisać użytkownika w bazie danych")
    void save_ShouldPersistUser() {
        User saved = userRepository.save(sampleUser);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Powinno znaleźć użytkownika po ID")
    void findById_ShouldReturnUser() {
        User saved = userRepository.save(sampleUser);
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Powinno zwrócić wszystkich użytkowników")
    void findAll_ShouldReturnList() {
        userRepository.save(new User("user1", "pass", "u1@ex.com", UserRole.USER));
        userRepository.save(new User("user2", "pass", "u2@ex.com", UserRole.USER));

        List<User> users = userRepository.findAll();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Powinno zaktualizować dane użytkownika")
    void update_ShouldChangeData() {
        User saved = userRepository.save(sampleUser);
        saved.setEmail("newemail@example.com");
        userRepository.saveAndFlush(saved);

        User updated = userRepository.findById(saved.getId()).get();
        assertThat(updated.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    @DisplayName("Powinno usunąć użytkownika z bazy")
    void delete_ShouldRemoveUser() {
        User saved = userRepository.save(sampleUser);
        userRepository.deleteById(saved.getId());

        Optional<User> deleted = userRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Powinno sprawdzić czy użytkownik istnieje po ID")
    void existsById_ShouldReturnTrue() {
        User saved = userRepository.save(sampleUser);
        boolean exists = userRepository.existsById(saved.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Powinno zwrócić liczbę użytkowników")
    void count_ShouldReturnCorrectCount() {
        userRepository.save(new User("u1", "p", "u1@test.com", UserRole.USER));
        userRepository.save(new User("u2", "p", "u2@test.com", UserRole.USER));
        
        long count = userRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Powinno zapisać wielu użytkowników naraz")
    void saveAll_ShouldPersistMultipleUsers() {
        List<User> users = List.of(
            new User("userA", "pass", "a@test.com", UserRole.USER),
            new User("userB", "pass", "b@test.com", UserRole.USER)
        );
        
        List<User> saved = userRepository.saveAll(users);
        assertThat(saved).hasSize(2);
    }

    @Test
    @DisplayName("Powinno usunąć wszystkich użytkowników")
    void deleteAll_ShouldClearDatabase() {
        userRepository.save(sampleUser);
        userRepository.deleteAll();
        
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy użytkownik istnieje po ID (negatywny)")
    void existsById_ShouldReturnFalseWhenNotExists() {
        boolean exists = userRepository.existsById(999L);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Powinno pobrać użytkownika używając JdbcTemplate i RowMapper")
    void customQueryWithRowMapper_ShouldMapCorrectly() {
        userRepository.save(sampleUser);

        RowMapper<User> userRowMapper = (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            return user;
        };

        String sql = "SELECT id, username, email FROM users WHERE username = ?";
        User user = jdbcTemplate.queryForObject(sql, userRowMapper, "testuser");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Powinno znaleźć użytkownika po nazwie użytkownika")
    void findByUsername_ShouldWork() {
        userRepository.save(sampleUser);
        Optional<User> found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Powinno znaleźć użytkownika po emailu")
    void findByEmail_ShouldWork() {
        userRepository.save(sampleUser);
        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Powinno znaleźć użytkowników zawierających frazę w nazwie")
    void findByUsernameContaining_ShouldWork() {
        userRepository.save(sampleUser);
        Page<User> result = userRepository.findByUsernameContaining("test", PageRequest.of(0, 10));
        assertThat(result.getContent()).isNotEmpty();
    }
}
