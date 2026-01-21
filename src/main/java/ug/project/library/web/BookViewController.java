package ug.project.library.web;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ug.project.library.service.BookService;
import ug.project.library.dto.BookDto;
import ug.project.library.service.RatingService;

@Controller
@RequestMapping("/books")
public class BookViewController {
    
    private final BookService bookService;
    private final RatingService ratingService;

    public BookViewController(BookService bookService, RatingService ratingService) {
        this.bookService = bookService;
        this.ratingService = ratingService;
    }

    @GetMapping("/{id}")
    public String bookDetail(@PathVariable Long id, Model model, Pageable pageable) {
        BookDto book = bookService.getBookDtoById(id);
        long numRatings = ratingService.getAllRatingsForBook(id, pageable).getTotalElements();
        model.addAttribute("book", book);
        model.addAttribute("numRatings", numRatings);
        return "book-details";
    }
}