//package ug.project.library.web;
//
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import ug.project.library.dto.CommentDto;
//import ug.project.library.service.CommentService;
//import ug.project.library.service.RatingService;
//import org.springframework.web.bind.annotation.GetMapping;
//import ug.project.library.dto.RatingDto;
//import org.springframework.data.domain.Page;
//
//@Controller
//public class ReviewsViewController {
//
//    private final RatingService ratingService;
//    private final CommentService commentService;
//
//    public ReviewsViewController(RatingService ratingService, CommentService commentService) {
//        this.ratingService = ratingService;
//        this.commentService = commentService;
//    }
//
//    @GetMapping("/reviews/{id}")
//    public String reviewsPage(@PathVariable Long id, Pageable pageable, Model model){
//        Page<RatingDto> ratings = ratingService.getAllRatingsForBook(id, pageable);
//        Page<CommentDto> comments = commentService.getAllCommentsForBook(id, pageable);
//        model.addAtribute("ratings", ratings);
//        model.addAtribute("comments", comments);
//        return "reviews";
//    }
//
//
//}
