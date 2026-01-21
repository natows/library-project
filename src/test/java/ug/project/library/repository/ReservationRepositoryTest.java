package ug.project.library.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Reservation;
import ug.project.library.model.entity.User;
import ug.project.library.model.enumerate.ReservationStatus;
import ug.project.library.model.enumerate.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User testUser;
    private Book testBook;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("user", "pass", "user@test.com", UserRole.USER);
        testUser = userRepository.save(testUser);

        testBook = new Book();
        testBook.setTitle("Title");
        testBook.setPublisher("Publisher");
        testBook = bookRepository.save(testBook);

        testReservation = new Reservation(ReservationStatus.OCZEKUJĄCA, LocalDateTime.now(), testUser, testBook, LocalDateTime.now().plusDays(7));
    }

    @Test
    @DisplayName("Powinno zapisać rezerwację")
    void save_ShouldPersistReservation() {
        Reservation saved = reservationRepository.save(testReservation);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.OCZEKUJĄCA);
    }

    @Test
    @DisplayName("Powinno znaleźć rezerwację po ID")
    void findById_ShouldReturnReservation() {
        Reservation saved = reservationRepository.save(testReservation);
        Optional<Reservation> found = reservationRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Powinno zwrócić wszystkie rezerwacje")
    void findAll_ShouldReturnList() {
        reservationRepository.save(testReservation);
        List<Reservation> all = reservationRepository.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    @DisplayName("Powinno zaktualizować status rezerwacji")
    void update_ShouldChangeStatus() {
        Reservation saved = reservationRepository.save(testReservation);
        saved.setStatus(ReservationStatus.WYPOŻYCZONA);
        reservationRepository.saveAndFlush(saved);

        Reservation updated = reservationRepository.findById(saved.getId()).get();
        assertThat(updated.getStatus()).isEqualTo(ReservationStatus.WYPOŻYCZONA);
    }

    @Test
    @DisplayName("Powinno usunąć rezerwację")
    void delete_ShouldRemoveReservation() {
        Reservation saved = reservationRepository.save(testReservation);
        reservationRepository.deleteById(saved.getId());
        assertThat(reservationRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Powinno sprawdzić czy rezerwacja istnieje")
    void existsById_ShouldReturnTrue() {
        Reservation saved = reservationRepository.save(testReservation);
        assertThat(reservationRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("Powinno zwrócić liczbę rezerwacji")
    void count_ShouldReturnCorrectCount() {
        reservationRepository.save(testReservation);
        assertThat(reservationRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Powinno zapisać wiele rezerwacji")
    void saveAll_ShouldPersistMultiple() {
        Reservation r2 = new Reservation(ReservationStatus.ZWRÓCONA, LocalDateTime.now(), testUser, testBook, LocalDateTime.now());
        List<Reservation> saved = reservationRepository.saveAll(List.of(testReservation, r2));
        assertThat(saved).hasSize(2);
    }

    @Test
    @DisplayName("Powinno usunąć wszystkie rezerwacje")
    void deleteAll_ShouldClearDatabase() {
        reservationRepository.save(testReservation);
        reservationRepository.deleteAll();
        assertThat(reservationRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy rezerwacja istnieje (negatywny)")
    void existsById_ShouldReturnFalse() {
        assertThat(reservationRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("Powinno pobrać rezerwację przez JdbcTemplate i RowMapper")
    void customQueryWithRowMapper_ShouldWork() {
        Reservation saved = reservationRepository.save(testReservation);
        RowMapper<Reservation> mapper = (rs, rowNum) -> {
            Reservation r = new Reservation();
            r.setId(rs.getLong("id"));
            r.setStatus(ReservationStatus.valueOf(rs.getString("status")));
            return r;
        };
        Reservation r = jdbcTemplate.queryForObject("SELECT id, status FROM reservations WHERE id = ?", mapper, saved.getId());
        assertThat(r).isNotNull();
        assertThat(r.getStatus()).isEqualTo(ReservationStatus.OCZEKUJĄCA);
    }

    @Test
    @DisplayName("Powinno znaleźć wygasłe rezerwacje")
    void findExpiredReservations_ShouldWork() {
        testReservation.setDeadline(LocalDateTime.now().minusDays(1));
        reservationRepository.save(testReservation);
        List<Reservation> expired = reservationRepository.findExpiredReservations(LocalDateTime.now());
        assertThat(expired).isNotEmpty();
    }

    @Test
    @DisplayName("Powinno sprawdzić czy wypożyczenie istnieje dla książki i użytkownika")
    void existsLoanByBookIdAndUserId_ShouldWork() {
        testReservation.setStatus(ReservationStatus.WYPOŻYCZONA);
        reservationRepository.save(testReservation);
        boolean exists = reservationRepository.existsLoanByBookIdAndUserId(testBook.getId(), testUser.getId());
        assertThat(exists).isTrue();
    }
}
