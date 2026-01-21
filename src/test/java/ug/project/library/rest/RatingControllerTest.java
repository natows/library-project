package ug.project.library.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ug.project.library.controller.RatingController;
import ug.project.library.dto.RatingDto;
import ug.project.library.service.RatingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
public class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void shouldReturnAllRatings() throws Exception {
        RatingDto rating = createSampleRatingDto(1L, 5);
        PageRequest pageable = PageRequest.of(0, 10);
        when(ratingService.getAllRatings(any())).thenReturn(new PageImpl<>(Collections.singletonList(rating), pageable, 1));

        mockMvc.perform(get("/api/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].score").value(5));
    }

    @Test
    @WithMockUser
    public void shouldReturnRatingById() throws Exception {
        RatingDto rating = createSampleRatingDto(1L, 5);
        when(ratingService.getRatingDtoById(1L)).thenReturn(rating);

        mockMvc.perform(get("/api/ratings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(5));
    }

    @Test
    @WithMockUser
    public void shouldAddRating() throws Exception {
        RatingDto rating = createSampleRatingDto(null, 4);
        RatingDto savedRating = createSampleRatingDto(1L, 4);
        when(ratingService.addRating(any(RatingDto.class))).thenReturn(savedRating);

        mockMvc.perform(post("/api/ratings/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.score").value(4));
    }

    @Test
    @WithMockUser
    public void shouldUpdateRating() throws Exception {
        RatingDto rating = createSampleRatingDto(1L, 3);
        when(ratingService.updateRating(eq(1L), any(RatingDto.class))).thenReturn(rating);

        mockMvc.perform(put("/api/ratings/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(3));
    }

    @Test
    @WithMockUser
    public void shouldDeleteRating() throws Exception {
        mockMvc.perform(delete("/api/ratings/delete/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    private RatingDto createSampleRatingDto(Long id, Integer score) {
        return new RatingDto(id, 1L, "user", 1L, "Book", score, LocalDateTime.now(), LocalDateTime.now());
    }
}
