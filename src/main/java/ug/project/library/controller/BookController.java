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

import ug.project.library.dto.BookDto;
import ug.project.library.service.BookService;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Zarządzanie książkami")
public class BookController{

    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    @Operation(summary = "Pobierz wszystkie książki z paginacją")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista książek została pobrana")
    })
    @GetMapping("/all")
    public ResponseEntity<Page<BookDto>> getAllBooks(Pageable pageable){
        Page<BookDto> booksDto = bookService.getAllBooksDto(pageable);
        return ResponseEntity.ok(booksDto);
    }

    @Operation(summary = "Pobierz książkę po ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Książka znaleziona"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono książki o podanym ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id){
        BookDto bookDto = bookService.getBookDtoById(id);
        return ResponseEntity.ok(bookDto);
    }

    @Operation(summary = "Dodaj nową książkę")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Książka została dodana"),
        @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PostMapping("/add")
    public ResponseEntity<BookDto> addBook (@Valid @RequestBody BookDto book) {
        BookDto savedBook = bookService.addBook(book);
        return ResponseEntity
            .created(URI.create("/api/v1/books/" + savedBook.getId()))
            .body(savedBook);
    }

    @Operation(summary = "Aktualizuj książkę po ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Książka została zaktualizowana"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono książki o podanym ID"),
        @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejściowe")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id,@Valid  @RequestBody BookDto bookDto) {
        BookDto updatedBook = bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(updatedBook);
    }

    @Operation(summary = "Usuń książkę po ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Książka została usunięta"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono książki o podanym ID")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

}
