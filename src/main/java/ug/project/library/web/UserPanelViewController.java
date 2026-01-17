package ug.project.library.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ug.project.library.service.AuthService;
import ug.project.library.service.ReservationService;
import ug.project.library.service.BookService;
import ug.project.library.dto.BookDto;
import ug.project.library.model.entity.Reservation;

@Controller
@RequestMapping("/user")
public class UserPanelViewController {

    private final ReservationService reservationService;
    private final AuthService authService;


    public UserPanelViewController(ReservationService reservationService, AuthService authService) {
        this.reservationService = reservationService;
        this.authService = authService;
    }

    @GetMapping("/my-reservations")
    public String myReservations(Model model) {
        Long userId = authService.getCurrentUserId();
        List<Reservation> activeReservations = reservationService.getUserActiveReservations(userId);
        
        model.addAttribute("reservations", activeReservations);
        return "user/my-reservations";
    }
}