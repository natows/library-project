package ug.project.library.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ug.project.library.service.BookService;
import ug.project.library.service.ReservationService;
import ug.project.library.dto.BookDto;
import ug.project.library.model.entity.Reservation;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/reservation")
public class ReservationViewController {
    
    private final BookService bookService;
    private final ReservationService reservationService;

    public ReservationViewController(BookService bookService, ReservationService reservationService) {
        this.bookService = bookService;
        this.reservationService = reservationService;
    }

    @GetMapping("/{bookId}")
    public String reservationPage(@PathVariable Long bookId, Model model) {
        BookDto book = bookService.getBookDtoById(bookId);
        model.addAttribute("book", book);
        return "reservation";
    }

    @PostMapping("/{bookId}")
    public String makeReservation(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
        try {
            Reservation reservation = reservationService.createNewReservation(bookId);
            redirectAttributes.addFlashAttribute("success", "Rezerwacja została utworzona pomyślnie!");
            return "redirect:/my-books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservation/" + bookId;
        }
    }
}