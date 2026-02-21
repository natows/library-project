package ug.project.library.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ug.project.library.dto.BookDto;
import ug.project.library.service.BookService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HomeController.class, properties = "spring.thymeleaf.check-template-location=false")
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    @WithMockUser
    public void shouldReturnIndexPageWithBooks() throws Exception {
        BookDto book = new BookDto();
        book.setTitle("Test Book");
        Page<BookDto> booksPage = new PageImpl<>(Collections.singletonList(book));

        when(bookService.getAllBooksDto(any(Pageable.class))).thenReturn(booksPage);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("currentSort"));
    }

    @Test
    @WithMockUser
    public void shouldReturnIndexPageWithPopularBooks() throws Exception {
        BookDto book = new BookDto();
        book.setTitle("Popular Book");
        List<BookDto> popularBooks = Collections.singletonList(book);

        when(bookService.getTopRatedBooks(anyInt())).thenReturn(popularBooks);

        mockMvc.perform(get("/").param("tab", "popular"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("currentSort", "title"));
    }

    @Test
    @WithMockUser
    public void shouldReturnSearchResults() throws Exception {
        BookDto book = new BookDto();
        book.setTitle("Search Result");
        Page<BookDto> booksPage = new PageImpl<>(Collections.singletonList(book));

        when(bookService.searchBooks(anyString(), any(), any(), any(Pageable.class))).thenReturn(booksPage);

        mockMvc.perform(get("/").param("title", "Search"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("books"));
    }
    @Test
    @WithMockUser
    @SuppressWarnings("unchecked")
    public void shouldReturnSearchResultsByAuthor() throws Exception {
        Page<BookDto> booksPage = new PageImpl<>(Collections.emptyList());
        when(bookService.searchBooks(any(), anyString(), any(), any(Pageable.class))).thenReturn(booksPage);

        mockMvc.perform(get("/").param("author", "Some Author"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser
    @SuppressWarnings("unchecked")
    public void shouldReturnSearchResultsByKeyword() throws Exception {
        Page<BookDto> booksPage = new PageImpl<>(Collections.emptyList());
        when(bookService.searchBooks(any(), any(), anyString(), any(Pageable.class))).thenReturn(booksPage);

        mockMvc.perform(get("/").param("keyword", "Some Keyword"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser
    @SuppressWarnings("unchecked")
    public void shouldHandleBlankSearchParams() throws Exception {
        Page<BookDto> booksPage = new PageImpl<>(Collections.emptyList());
        when(bookService.getAllBooksDto(any(Pageable.class))).thenReturn(booksPage);

        mockMvc.perform(get("/")
                        .param("title", " ")
                        .param("author", "")
                        .param("keyword", "  "))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser
    public void shouldSortByRating() throws Exception {
        Page<BookDto> booksPage = new PageImpl<>(Collections.emptyList());
        when(bookService.getAllBooksDto(any(Pageable.class))).thenReturn(booksPage);

        mockMvc.perform(get("/").param("sortBy", "rating"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentSort", "rating"));
    }

    @Test
    @WithMockUser
    public void shouldSortByYear() throws Exception {
        Page<BookDto> booksPage = new PageImpl<>(Collections.emptyList());
        when(bookService.getAllBooksDto(any(Pageable.class))).thenReturn(booksPage);

        mockMvc.perform(get("/").param("sortBy", "year"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentSort", "year"));
    }

    @Test
    @WithMockUser
    public void shouldSortByDefault() throws Exception {
        Page<BookDto> booksPage = new PageImpl<>(Collections.emptyList());
        when(bookService.getAllBooksDto(any(Pageable.class))).thenReturn(booksPage);

        mockMvc.perform(get("/").param("sortBy", "unknown"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentSort", "unknown"));
    }
}
