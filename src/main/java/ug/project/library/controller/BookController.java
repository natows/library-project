package ug.project.library.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ug.project.library.dto.BookDto;
import ug.project.library.service.BookService;





@RestController
@RequestMapping("api/books")
public class BookController{

    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }



    @GetMapping("/all")
    public ResponseEntity<Page<BookDto>> getAllBooks(Pageable pageable){
        Page<BookDto> booksDto = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(booksDto);
    }

    @PostMapping("/add")
    public ResponseEntity<BookDto> addBook (@RequestBody BookDto book) {
        BookDto savedBook = bookService.addBook(book);
        return ResponseEntity
            .created(URI.create("/api/books/" + savedBook.getId()))
            .body(savedBook);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        BookDto updatedBook = bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(updatedBook);
        
        
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void>  deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

} 