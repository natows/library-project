package ug.project.library.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import ug.project.library.dto.CommentDto;
import ug.project.library.dto.RatingDto;
import ug.project.library.service.RatingService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;


@Controller
public class RatingViewController {

    private final RatingService ratingService;

    public RatingViewController(RatingService ratingService) {
        this.ratingService = ratingService;
    }



    @GetMapping("rate/{bookId}")
    public String rateBook(@PathVariable Long bookId, Model model) {
        model.addAttribute("bookId", bookId);
        model.addAttribute("ratingDto", new RatingDto());
        model.addAttribute("commentDto", new CommentDto());
        return "rate";
    }

    @PostMapping("rate/{bookId}")
    public String submitRating(@PathVariable Long bookId,
                               @ModelAttribute @Valid RatingDto ratingDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookId", bookId);
            return "rate";
        }
        try {
            ratingDto.setBookId(bookId);
            ratingService.addRating(ratingDto);
            redirectAttributes.addFlashAttribute("success", "Ocena została dodana!");
        } catch (Exception e) {
            bindingResult.reject("ratingError", e.getMessage());
            model.addAttribute("bookId", bookId);
            return "rate";
        }
        return "redirect:/books/" + bookId;
    }
}
