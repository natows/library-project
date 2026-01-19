package ug.project.library.controller;

import java.net.URI;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import ug.project.library.dto.RatingDto;
import ug.project.library.service.RatingService;

@RestController
@RequestMapping("/api/ratings")
@Tag(name = "Ratings", description = "Zarządzanie ocenami")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService){
        this.ratingService = ratingService;
    }

    @Operation(summary = "Pobierz wszystkie oceny z paginacją")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista ocen została pobrana")
    })
    @GetMapping
    public ResponseEntity<Page<RatingDto>> getAllRatings(Pageable pageable) {
        Page<RatingDto> ratingsDto = ratingService.getAllRatings(pageable);
        return ResponseEntity.ok(ratingsDto);
    }

    @Operation(summary = "Pobierz ocenę po ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ocena znaleziona"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono oceny o podanym ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RatingDto> getRatingById(@PathVariable Long id){
        RatingDto ratingDto = ratingService.getRatingDtoById(id);
        return ResponseEntity.ok(ratingDto);
    }

    @Operation(summary = "Dodaj nową ocenę")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ocena została dodana"),
        @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe"),
        @ApiResponse(responseCode = "403", description = "Brak uprawnień lub naruszenie zasad dodawania ocen")
    })
    @PostMapping("/add")
    public ResponseEntity<RatingDto> addRating (@Valid @RequestBody RatingDto ratingDto) {
        RatingDto savedRating = ratingService.addRating(ratingDto);
        return ResponseEntity
            .created(URI.create("/api/v1/ratings/" + savedRating.getId()))
            .body(savedRating);
    }

    @Operation(summary = "Aktualizuj ocenę po ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ocena została zaktualizowana"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono oceny o podanym ID"),
        @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<RatingDto> updateRating(@PathVariable Long id, @Valid @RequestBody RatingDto ratingDto) {
        RatingDto updatedRating = ratingService.updateRating(id, ratingDto);
        return ResponseEntity.ok(updatedRating);
    }

    @Operation(summary = "Usuń ocenę po ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ocena została usunięta"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono oceny o podanym ID")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id){
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}