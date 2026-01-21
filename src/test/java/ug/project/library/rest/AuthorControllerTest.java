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
import ug.project.library.controller.AuthorController;
import ug.project.library.dto.AuthorDto;
import ug.project.library.service.AuthorService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void shouldReturnAllAuthors() throws Exception {
        AuthorDto author = new AuthorDto(1L, "John", "Doe");
        PageRequest pageable = PageRequest.of(0, 10);
        when(authorService.getAllAuthorDto(any())).thenReturn(new PageImpl<>(Collections.singletonList(author), pageable, 1));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("John"))
                .andExpect(jsonPath("$.content[0].surname").value("Doe"));
    }

    @Test
    @WithMockUser
    public void shouldReturnAuthorById() throws Exception {
        AuthorDto author = new AuthorDto(1L, "John", "Doe");
        when(authorService.getAuthorDtoById(1L)).thenReturn(author);

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldAddAuthor() throws Exception {
        AuthorDto author = new AuthorDto(null, "Jane", "Doe");
        AuthorDto savedAuthor = new AuthorDto(1L, "Jane", "Doe");
        when(authorService.addAuthor(any(AuthorDto.class))).thenReturn(savedAuthor);

        mockMvc.perform(post("/api/authors/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/authors/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jane"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateAuthor() throws Exception {
        AuthorDto author = new AuthorDto(1L, "John", "Updated");
        when(authorService.updateAuthor(eq(1L), any(AuthorDto.class))).thenReturn(author);

        mockMvc.perform(put("/api/authors/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value("Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/api/authors/delete/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
