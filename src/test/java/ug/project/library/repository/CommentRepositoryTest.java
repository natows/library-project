package ug.project.library.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Comment;
import ug.project.library.model.entity.User;
import ug.project.library.model.enumerate.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User testUser;
    private Book testBook;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        commentRepository.flush();
        bookRepository.deleteAll();
        bookRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        testUser = new User("user", "pass", "user@test.com", UserRole.USER);
        testUser = userRepository.saveAndFlush(testUser);

        testBook = new Book();
        testBook.setTitle("Title");
        testBook.setPublisher("Publisher");
        testBook = bookRepository.saveAndFlush(testBook);

        testComment = new Comment("Great book!", testUser, testBook);
    }

    @Test
    @DisplayName("Powinno zapisać komentarz")
    void save_ShouldPersistComment() {
        Comment saved = commentRepository.save(testComment);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isEqualTo("Great book!");
    }

    @Test
    @DisplayName("Powinno znaleźć komentarz po ID")
    void findById_ShouldReturnComment() {
        Comment saved = commentRepository.save(testComment);
        Optional<Comment> found = commentRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Powinno zwrócić wszystkie komentarze")
    void findAll_ShouldReturnList() {
        commentRepository.save(testComment);
        List<Comment> all = commentRepository.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    @DisplayName("Powinno zaktualizować treść komentarza")
    void update_ShouldChangeContent() {
        Comment saved = commentRepository.save(testComment);
        saved.setContent("Updated content");
        commentRepository.saveAndFlush(saved);

        Comment updated = commentRepository.findById(saved.getId()).get();
        assertThat(updated.getContent()).isEqualTo("Updated content");
    }

    @Test
    @DisplayName("Powinno usunąć komentarz")
    void delete_ShouldRemoveComment() {
        Comment saved = commentRepository.save(testComment);
        commentRepository.deleteById(saved.getId());
        assertThat(commentRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Powinno sprawdzić czy komentarz istnieje")
    void existsById_ShouldReturnTrue() {
        Comment saved = commentRepository.save(testComment);
        assertThat(commentRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("Powinno zwrócić liczbę komentarzy")
    void count_ShouldReturnCorrectCount() {
        commentRepository.save(testComment);
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Powinno zapisać wiele komentarzy")
    void saveAll_ShouldPersistMultiple() {
        Comment c2 = new Comment("Nice", testUser, testBook);
        List<Comment> saved = commentRepository.saveAll(List.of(testComment, c2));
        assertThat(saved).hasSize(2);
    }

    @Test
    @DisplayName("Powinno usunąć wszystkie komentarze")
    void deleteAll_ShouldClearDatabase() {
        commentRepository.save(testComment);
        commentRepository.deleteAll();
        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy komentarz istnieje (negatywny)")
    void existsById_ShouldReturnFalse() {
        assertThat(commentRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("Powinno pobrać komentarz przez JdbcTemplate i RowMapper")
    void customQueryWithRowMapper_ShouldWork() {
        Comment saved = commentRepository.save(testComment);
        RowMapper<Comment> mapper = (rs, rowNum) -> {
            Comment c = new Comment();
            c.setId(rs.getLong("id"));
            c.setContent(rs.getString("content"));
            return c;
        };
        Comment c = jdbcTemplate.queryForObject("SELECT id, content FROM comments WHERE id = ?", mapper, saved.getId());
        assertThat(c).isNotNull();
        assertThat(c.getContent()).isEqualTo("Great book!");
    }
}
