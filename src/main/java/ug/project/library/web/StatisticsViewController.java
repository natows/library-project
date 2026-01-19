package ug.project.library.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.User;
import ug.project.library.service.StatisticsService;

@Controller
@RequestMapping("/admin")
public class StatisticsViewController {

    private final StatisticsService statisticsService;

    public StatisticsViewController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/stats")
    public String statistics(
            @RequestParam(defaultValue = "0") int booksPage,
            @RequestParam(defaultValue = "0") int authorsPage,
            @RequestParam(defaultValue = "0") int usersPage,
            Model model) {
        
        // Najpopularniejsze książki
        Pageable booksPageable = PageRequest.of(booksPage, 10);
        Page<Book> books = statisticsService.getMostPopularBooks(booksPageable);
        
        // Najpopularniejsi autorzy
        Pageable authorsPageable = PageRequest.of(authorsPage, 10);
        Page<Author> authors = statisticsService.getMostPopularAuthors(authorsPageable);
        
        // Najaktywniejsi użytkownicy
        Pageable usersPageable = PageRequest.of(usersPage, 10);
        Page<User> users = statisticsService.getUsersWithMostLoans(usersPageable);
        
        model.addAttribute("books", books);
        model.addAttribute("authors", authors);
        model.addAttribute("users", users);
        model.addAttribute("booksPage", booksPage);
        model.addAttribute("authorsPage", authorsPage);
        model.addAttribute("usersPage", usersPage);
        
        return "admin/stats";
    }
}