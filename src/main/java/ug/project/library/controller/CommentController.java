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
import ug.project.library.dto.CommentDto;
import ug.project.library.service.CommentService;

import java.net.URI;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Zarządzanie komentarzami")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Pobierz wszystkie komentarze z paginacją")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista komentarzy została pobrana")
    })
    @GetMapping
    public ResponseEntity<Page<CommentDto>> getAllComments(Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllComments(pageable));
    }

    @Operation(summary = "Pobierz komentarz po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Komentarz znaleziony"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono komentarza o podanym ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @Operation(summary = "Dodaj nowy komentarz")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Komentarz został dodany"),
            @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PostMapping("/add")
    public ResponseEntity<CommentDto> addComment(@Valid @RequestBody CommentDto commentDto) {
        CommentDto savedComment = commentService.addComment(commentDto);
        return ResponseEntity
                .created(URI.create("/api/v1/comments/" + savedComment.getId()))
                .body(savedComment);
    }

    @Operation(summary = "Aktualizuj komentarz po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Komentarz został zaktualizowany"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono komentarza o podanym ID"),
            @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(commentService.updateComment(id, commentDto));
    }

    @Operation(summary = "Usuń komentarz po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Komentarz został usunięty"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono komentarza o podanym ID")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
