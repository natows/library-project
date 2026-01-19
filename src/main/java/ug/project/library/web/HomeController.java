package ug.project.library.web;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ug.project.library.dto.BookDto;
import ug.project.library.service.BookService;

@Controller
public class HomeController {

    private final BookService bookService;

    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String title,
                      @RequestParam(required = false) String author,
                      @RequestParam(required = false) String keyword,
                      @RequestParam(required = false, defaultValue = "title") String sortBy,
                      @RequestParam(required = false) String tab,
                      @RequestParam(required = false, defaultValue = "0") int page,
                      @RequestParam(required = false, defaultValue = "20") int size,
                      Model model) {


        Page<BookDto> books;
        
        if ("popular".equals(tab)) {
            List<BookDto> popularBooks = bookService.getTopRatedBooks(20);
            books = new PageImpl<>(popularBooks, PageRequest.of(0, popularBooks.size()), popularBooks.size());
        } else {
            Pageable pageable = createPageableWithSort(sortBy, page, size);
            
            boolean hasSearchParams = (title != null && !title.isBlank()) || 
                                     (author != null && !author.isBlank()) || 
                                     (keyword != null && !keyword.isBlank());
            
            if (hasSearchParams) {
                books = bookService.searchBooks(title, author, keyword, pageable);
            } else {
                books = bookService.getAllBooksDto(pageable);
            }
        }
        
        model.addAttribute("books", books);
        model.addAttribute("currentSort", sortBy);
        return "index";
    }


    private Pageable createPageableWithSort(String sortBy, int page, int size) {
        Sort sort;
        
        switch (sortBy != null ? sortBy : "title") {
            case "rating":
                sort = Sort.by(Sort.Direction.DESC, "avgRating");
                break;
            case "year":
                sort = Sort.by(Sort.Direction.DESC, "yearPublished");
                break;
            case "title":
            default:
                sort = Sort.by(Sort.Direction.ASC, "title");
                break;
        }
        
        return PageRequest.of(page, size, sort);
    }
}