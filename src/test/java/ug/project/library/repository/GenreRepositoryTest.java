package ug.project.library.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ug.project.library.model.entity.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Genre sampleGenre;

    @BeforeEach
    void setUp() {
        genreRepository.deleteAll();
        genreRepository.flush();
        sampleGenre = new Genre();
        sampleGenre.setName("Fantasy");
    }

    @Test
    @DisplayName("Powinno zapisać gatunek")
    void save_ShouldPersistGenre() {
        Genre saved = genreRepository.save(sampleGenre);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Fantasy");
    }

    @Test
    @DisplayName("Powinno znaleźć gatunek po ID")
    void findById_ShouldReturnGenre() {
        Genre saved = genreRepository.save(sampleGenre);
        Optional<Genre> found = genreRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Powinno zwrócić wszystkie gatunki")
    void findAll_ShouldReturnList() {
        genreRepository.save(sampleGenre);
        List<Genre> all = genreRepository.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    @DisplayName("Powinno zaktualizować nazwę gatunku")
    void update_ShouldChangeName() {
        Genre saved = genreRepository.save(sampleGenre);
        saved.setName("Sci-Fi");
        genreRepository.saveAndFlush(saved);

        Genre updated = genreRepository.findById(saved.getId()).get();
        assertThat(updated.getName()).isEqualTo("Sci-Fi");
    }

    @Test
    @DisplayName("Powinno usunąć gatunek")
    void delete_ShouldRemoveGenre() {
        Genre saved = genreRepository.save(sampleGenre);
        genreRepository.deleteById(saved.getId());
        assertThat(genreRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Powinno sprawdzić czy gatunek istnieje")
    void existsById_ShouldReturnTrue() {
        Genre saved = genreRepository.save(sampleGenre);
        assertThat(genreRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("Powinno zwrócić liczbę gatunków")
    void count_ShouldReturnCorrectCount() {
        genreRepository.save(sampleGenre);
        assertThat(genreRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Powinno zapisać wiele gatunków")
    void saveAll_ShouldPersistMultiple() {
        Genre g2 = new Genre(); g2.setName("Horror");
        List<Genre> saved = genreRepository.saveAll(List.of(sampleGenre, g2));
        assertThat(saved).hasSize(2);
    }

    @Test
    @DisplayName("Powinno usunąć wszystkie gatunki")
    void deleteAll_ShouldClearDatabase() {
        genreRepository.save(sampleGenre);
        genreRepository.deleteAll();
        assertThat(genreRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy gatunek istnieje (negatywny)")
    void existsById_ShouldReturnFalse() {
        assertThat(genreRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("Powinno pobrać gatunek przez JdbcTemplate i RowMapper")
    void customQueryWithRowMapper_ShouldWork() {
        genreRepository.save(sampleGenre);
        RowMapper<Genre> mapper = (rs, rowNum) -> {
            Genre g = new Genre();
            g.setId(rs.getLong("id"));
            g.setName(rs.getString("name"));
            return g;
        };
        Genre g = jdbcTemplate.queryForObject("SELECT id, name FROM genres WHERE name = ?", mapper, "Fantasy");
        assertThat(g).isNotNull();
        assertThat(g.getName()).isEqualTo("Fantasy");
    }

    @Test
    @DisplayName("Powinno wyrzucić wyjątek przy próbie zapisu duplikatu gatunku")
    void save_ShouldThrowException_WhenDuplicateName() {
        genreRepository.saveAndFlush(sampleGenre);
        
        Genre duplicate = new Genre();
        duplicate.setName("Fantasy");
        
        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            genreRepository.saveAndFlush(duplicate);
        });
    }

    @Test
    @DisplayName("Powinno znaleźć gatunek po nazwie")
    void findByName_ShouldWork() {
        genreRepository.save(sampleGenre);
        Optional<Genre> found = genreRepository.findByName("Fantasy");
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Powinno znaleźć gatunki zawierające frazę")
    void findByNameContaining_ShouldWork() {
        genreRepository.save(sampleGenre);
        List<Genre> result = genreRepository.findByNameContaining("Fan");
        assertThat(result).isNotEmpty();
    }
}
