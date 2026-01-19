package ug.project.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.project.library.dto.GenreDto;
import ug.project.library.service.GenreService;

import java.net.URI;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genres", description = "Zarządzanie gatunkami")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @Operation(summary = "Pobierz wszystkie gatunki z paginacją")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista gatunków została pobrana")
    })
    @GetMapping
    public ResponseEntity<Page<GenreDto>> getAllGenres(Pageable pageable) {
        return ResponseEntity.ok(genreService.getAllGenres(pageable));
    }

    @Operation(summary = "Pobierz gatunek po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gatunek znaleziony"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono gatunku o podanym ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @Operation(summary = "Dodaj nowy gatunek")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gatunek został dodany"),
            @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PostMapping("/add")
    public ResponseEntity<GenreDto> addGenre(@Valid @RequestBody GenreDto genreDto) {
        GenreDto savedGenre = genreService.createGenre(genreDto);
        return ResponseEntity
                .created(URI.create("/api/v1/genres/" + savedGenre.getId()))
                .body(savedGenre);
    }

    @Operation(summary = "Aktualizuj gatunek po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gatunek został zaktualizowany"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono gatunku o podanym ID"),
            @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreDto genreDto) {
        return ResponseEntity.ok(genreService.updateGenre(id, genreDto));
    }

    @Operation(summary = "Usuń gatunek po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Gatunek został usunięty"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono gatunku o podanym ID")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
