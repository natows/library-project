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

import ug.project.library.dto.AuthorDto;
import ug.project.library.service.AuthorService;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Authors", description = "Zarządzanie autorami")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService){
        this.authorService = authorService;
    }

    @Operation(summary = "Pobierz wszystkich autorów z paginacją")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista autorów została pobrana")
    })
    @GetMapping
    public ResponseEntity<Page<AuthorDto>> getAllAuthors(Pageable pageable) {
        Page<AuthorDto> authorsDto = authorService.getAllAuthorDto(pageable);
        return ResponseEntity.ok(authorsDto);
    }

    @Operation(summary = "Pobierz autora po ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autor znaleziony"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono autora o podanym ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Long id){
        AuthorDto authorDto = authorService.getAuthorDtoById(id);
        return ResponseEntity.ok(authorDto);
    }

    @Operation(summary = "Dodaj nowego autora")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Autor został dodany"),
        @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PostMapping("/add")
    public ResponseEntity<AuthorDto> addAuthor (@Valid @RequestBody AuthorDto author) {
        AuthorDto savedAuthor = authorService.addAuthor(author);
        return ResponseEntity
            .created(URI.create("/api/v1/authors/" + savedAuthor.getId()))
            .body(savedAuthor);
    }

    @Operation(summary = "Aktualizuj autora po ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autor został zaktualizowany"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono autora o podanym ID"),
        @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDto authorDto) {
        AuthorDto updatedAuthor = authorService.updateAuthor(id, authorDto);
        return ResponseEntity.ok(updatedAuthor);
    }

    @Operation(summary = "Usuń autora po ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Autor został usunięty"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono autora o podanym ID")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id){
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
