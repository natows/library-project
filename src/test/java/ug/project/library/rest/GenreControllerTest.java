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
import ug.project.library.controller.GenreController;
import ug.project.library.dto.GenreDto;
import ug.project.library.service.GenreService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void shouldReturnAllGenres() throws Exception {
        GenreDto genre = new GenreDto(1L, "Fantasy");
        PageRequest pageable = PageRequest.of(0, 10);
        when(genreService.getAllGenres(any())).thenReturn(new PageImpl<>(Collections.singletonList(genre), pageable, 1));

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Fantasy"));
    }

    @Test
    @WithMockUser
    public void shouldReturnGenreById() throws Exception {
        GenreDto genre = new GenreDto(1L, "Fantasy");
        when(genreService.getGenreById(1L)).thenReturn(genre);

        mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fantasy"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldAddGenre() throws Exception {
        GenreDto genre = new GenreDto(null, "Sci-Fi");
        GenreDto savedGenre = new GenreDto(1L, "Sci-Fi");
        when(genreService.createGenre(any(GenreDto.class))).thenReturn(savedGenre);

        mockMvc.perform(post("/api/genres/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genre)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Sci-Fi"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateGenre() throws Exception {
        GenreDto genre = new GenreDto(1L, "Updated Genre");
        when(genreService.updateGenre(eq(1L), any(GenreDto.class))).thenReturn(genre);

        mockMvc.perform(put("/api/genres/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genre)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Genre"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteGenre() throws Exception {
        mockMvc.perform(delete("/api/genres/delete/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
