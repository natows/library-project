package ug.project.library.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ug.project.library.dto.GenreDto;
import ug.project.library.exceptions.GenreNotFoundException;
import ug.project.library.model.entity.Genre;
import ug.project.library.repository.GenreRepository;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository){
        this.genreRepository = genreRepository;
    }

    @Transactional(readOnly = true)
    public Page<GenreDto> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public GenreDto getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));
        return mapToDto(genre);
    }

    @Transactional
    public GenreDto createGenre(GenreDto genreDto) {
        Genre genre = new Genre();
        genre.setName(genreDto.getName());
        return mapToDto(genreRepository.save(genre));
    }

    @Transactional
    public GenreDto updateGenre(Long id, GenreDto genreDto) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));
        genre.setName(genreDto.getName());
        return mapToDto(genreRepository.save(genre));
    }

    @Transactional
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new GenreNotFoundException(id);
        }
        genreRepository.deleteById(id);
    }

    @Transactional
    public Genre findOrCreateGenre(String name) {
        return genreRepository
            .findByName(name)
            .orElseGet(() -> {
                Genre genre = new Genre();
                genre.setName(name);
                return genreRepository.save(genre);
            });
    }

    private GenreDto mapToDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
