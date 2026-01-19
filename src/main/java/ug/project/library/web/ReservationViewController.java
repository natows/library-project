package ug.project.library.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ug.project.library.service.BookService;
import ug.project.library.service.ReservationService;
import ug.project.library.dto.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservationViewController {
    
    private final BookService bookService;
    private final ReservationService reservationService;

    public ReservationViewController(BookService bookService, ReservationService reservationService) {
        this.bookService = bookService;
        this.reservationService = reservationService;
    }

    @GetMapping("/reservation/{bookId}")
    public String reservationPage(@PathVariable Long bookId, Model model) {
        BookDto book = bookService.getBookDtoById(bookId);
        model.addAttribute("book", book);
        return "reservation";
    }

    @PostMapping("/reservation/{bookId}")
    public String makeReservation(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
        try {
            ReservationDto reservation = reservationService.createNewReservation(bookId);
            redirectAttributes.addFlashAttribute("success", "Rezerwacja została utworzona pomyślnie!");
            return "redirect:/user/my-reservations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservation/" + bookId;
        }
    }

    @GetMapping("/admin/manage-reservations")
    public String manageReservationsPage(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "admin/manage-reservations";
    }

    @PostMapping("/reservation/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, @RequestParam(defaultValue = "user") String source, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("success", "Rezerwacja została anulowana");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nie udało się anulować rezerwacji: " + e.getMessage());
        }
        return source.equals("admin") ? "redirect:/admin/manage-reservations" : "redirect:/user/my-reservations";
    }

    @PostMapping("/reservation/{id}/confirm")
    public String confirmReservation(@PathVariable Long id, @RequestParam(defaultValue = "user") String source, RedirectAttributes redirectAttributes) {
        try {
            reservationService.confirmReservation(id);
            redirectAttributes.addFlashAttribute("success", "Rezerwacja została potwierdzona");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nie udało się potwierdzić rezerwacji: " + e.getMessage());
        }
        return source.equals("admin") ? "redirect:/admin/manage-reservations" : "redirect:/user/my-reservations";
    }

    @PostMapping("/reservation/{id}/borrow")
    public String borrowReservation(@PathVariable Long id, @RequestParam(defaultValue = "user") String source, RedirectAttributes redirectAttributes) {
        try {
            reservationService.borrowReservation(id);
            redirectAttributes.addFlashAttribute("success", "Książka została wypożyczona");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nie udało się wypożyczyć książki: " + e.getMessage());
        }
        return source.equals("admin") ? "redirect:/admin/manage-reservations" : "redirect:/user/my-reservations";
    }

    @PostMapping("/reservation/{id}/return")
    public String returnReservation(@PathVariable Long id, @RequestParam(defaultValue = "user") String source, RedirectAttributes redirectAttributes) {
        try {
            reservationService.returnReservation(id);
            redirectAttributes.addFlashAttribute("success", "Książka została zwrócona");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nie udało się zwrócić książki: " + e.getMessage());
        }
        return source.equals("admin") ? "redirect:/admin/manage-reservations" : "redirect:/user/my-reservations";
    }
}