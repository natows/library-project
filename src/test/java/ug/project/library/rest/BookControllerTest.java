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
import ug.project.library.controller.BookController;
import ug.project.library.dto.AuthorDto;
import ug.project.library.dto.BookDto;
import ug.project.library.dto.GenreDto;
import ug.project.library.service.BookService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void shouldReturnAllBooks() throws Exception {
        BookDto book = createSampleBookDto(1L, "Sample Book");
        PageRequest pageable = PageRequest.of(0, 10);
        when(bookService.getAllBooksDto(any())).thenReturn(new PageImpl<>(Collections.singletonList(book), pageable, 1));

        mockMvc.perform(get("/api/books/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Sample Book"));
    }

    @Test
    @WithMockUser
    public void shouldReturnBookById() throws Exception {
        BookDto book = createSampleBookDto(1L, "Sample Book");
        when(bookService.getBookDtoById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Book"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldAddBook() throws Exception {
        BookDto book = createSampleBookDto(null, "New Book");
        BookDto savedBook = createSampleBookDto(1L, "New Book");
        when(bookService.addBook(any(BookDto.class))).thenReturn(savedBook);

        mockMvc.perform(post("/api/books/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("New Book"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateBook() throws Exception {
        BookDto book = createSampleBookDto(1L, "Updated Book");
        when(bookService.updateBook(eq(1L), any(BookDto.class))).thenReturn(book);

        mockMvc.perform(put("/api/books/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/delete/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    private BookDto createSampleBookDto(Long id, String title) {
        AuthorDto author = new AuthorDto(1L, "Author", "Name");
        GenreDto genre = new GenreDto(1L, "Genre");
        return new BookDto(id, title, List.of(author), List.of(genre), 5.0, 2023, "Publisher", "url", 10);
    }
}
