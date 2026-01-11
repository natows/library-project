package ug.project.library.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ug.project.library.service.BookService;
import ug.project.library.dto.BookDto;

@Controller
@RequestMapping("/books")
public class BookViewController {
    
    private final BookService bookService;

    public BookViewController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    public String bookDetail(@PathVariable Long id, Model model) {
        BookDto book = bookService.getBookDtoById(id);
        model.addAttribute("book", book);
        return "book-details";
    }
}