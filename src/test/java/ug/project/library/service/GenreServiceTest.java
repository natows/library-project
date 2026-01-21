package ug.project.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ug.project.library.dto.GenreDto;
import ug.project.library.model.entity.Genre;
import ug.project.library.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    private Genre genre;
    private GenreDto genreDto;

    @BeforeEach
    void setUp() {
        genre = new Genre();
        genre.setId(1L);
        genre.setName("Fantasy");

        genreDto = new GenreDto(1L, "Fantasy");
    }

    @Test
    @DisplayName("getAllGenres should return page of DTOs")
    void getAllGenres_ShouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(genreRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(genre)));

        Page<GenreDto> result = genreService.getAllGenres(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Fantasy");
    }

    @Test
    @DisplayName("getGenreById should return DTO when found")
    void getGenreById_ShouldReturnDto_WhenFound() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        GenreDto result = genreService.getGenreById(1L);

        assertThat(result.getName()).isEqualTo("Fantasy");
    }

    @Test
    @DisplayName("getGenreById should throw exception when not found")
    void getGenreById_ShouldThrowException_WhenNotFound() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> genreService.getGenreById(1L));
    }

    @Test
    @DisplayName("createGenre should save and return DTO")
    void createGenre_ShouldSaveAndReturnDto() {
        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        GenreDto result = genreService.createGenre(genreDto);

        assertThat(result.getName()).isEqualTo("Fantasy");
        verify(genreRepository).save(any(Genre.class));
    }

    @Test
    @DisplayName("updateGenre should update and return DTO")
    void updateGenre_ShouldUpdateAndReturnDto() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        genreDto.setName("Sci-Fi");
        GenreDto result = genreService.updateGenre(1L, genreDto);

        assertThat(result.getName()).isEqualTo("Sci-Fi");
        verify(genreRepository).save(genre);
    }

    @Test
    @DisplayName("deleteGenre should delete when exists")
    void deleteGenre_ShouldDelete_WhenExists() {
        when(genreRepository.existsById(1L)).thenReturn(true);

        genreService.deleteGenre(1L);

        verify(genreRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteGenre should throw exception when not found")
    void deleteGenre_ShouldThrowException_WhenNotFound() {
        when(genreRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> genreService.deleteGenre(1L));
    }

    @Test
    @DisplayName("findOrCreateGenre should return existing genre")
    void findOrCreateGenre_ShouldReturnExistingGenre() {
        when(genreRepository.findByName("Fantasy")).thenReturn(Optional.of(genre));

        Genre result = genreService.findOrCreateGenre("Fantasy");

        assertThat(result).isEqualTo(genre);
        verify(genreRepository, never()).save(any(Genre.class));
    }

    @Test
    @DisplayName("findOrCreateGenre should create new genre when not found")
    void findOrCreateGenre_ShouldCreateNewGenre() {
        when(genreRepository.findByName("New")).thenReturn(Optional.empty());
        when(genreRepository.save(any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Genre result = genreService.findOrCreateGenre("New");

        assertThat(result.getName()).isEqualTo("New");
        verify(genreRepository).save(any(Genre.class));
    }
}
