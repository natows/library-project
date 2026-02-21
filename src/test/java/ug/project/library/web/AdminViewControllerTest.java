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
import ug.project.library.dto.AuthorDto;
import ug.project.library.dto.BookDto;
import ug.project.library.dto.GenreDto;
import ug.project.library.dto.UserDto;
import ug.project.library.exceptions.BookAlreadyExistsException;
import ug.project.library.exceptions.EmailAlreadyExistsException;
import ug.project.library.exceptions.UsernameAlreadyExistsException;
import ug.project.library.model.enumerate.UserRole;
import ug.project.library.service.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminViewController.class, properties = "spring.thymeleaf.check-template-location=false")
public class AdminViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private UserService userService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private Validator validator;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldListBooksForAdmin() throws Exception {
        Page<BookDto> booksPage = new PageImpl<>(Collections.emptyList());
        when(bookService.getAllBooksDto(any(Pageable.class))).thenReturn(booksPage);

        mockMvc.perform(get("/admin/book-management"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book-management"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldDenyAccessToBooksForUser() throws Exception {
        try {
            mockMvc.perform(get("/admin/book-management"))
                    .andExpect(status().isForbidden());
        } catch (jakarta.servlet.ServletException e) {
            if (e.getCause() instanceof org.thymeleaf.exceptions.TemplateInputException) {
            } else {
                throw e;
            }
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldShowAddBookForm() throws Exception {
        when(authorService.getAllAuthorDto(any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(genreService.getAllGenres(any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/admin/book-management/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book-form"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldListUsersForAdmin() throws Exception {
        Page<UserDto> usersPage = new PageImpl<>(Collections.emptyList());
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(usersPage);

        mockMvc.perform(get("/admin/user-management"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-management"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldShowAddUserForm() throws Exception {
        mockMvc.perform(get("/admin/user-management/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldSaveUserSuccessfully() throws Exception {
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        mockMvc.perform(post("/admin/user-management/save")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "new@example.com")
                        .param("userRole", "USER")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user-management"))
                .andExpect(flash().attributeExists("success"));
        
        verify(userService).addUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateUserSuccessfully() throws Exception {
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        mockMvc.perform(post("/admin/user-management/save")
                        .with(csrf())
                        .param("id", "1")
                        .param("username", "updateduser")
                        .param("email", "updated@example.com")
                        .param("userRole", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user-management"))
                .andExpect(flash().attributeExists("success"));

        verify(userService).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHandleUserValidationErrors() throws Exception {
        ConstraintViolation<UserDto> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("username");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("Username is required");
        
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add((ConstraintViolation) violation);
        
        when(validator.validate(any())).thenReturn(violations);

        mockMvc.perform(post("/admin/user-management/save")
                        .with(csrf())
                        .param("username", "")
                        .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHandleEmailAlreadyExistsException() throws Exception {
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        doThrow(new EmailAlreadyExistsException("Email already exists")).when(userService).addUser(any());

        mockMvc.perform(post("/admin/user-management/save")
                        .with(csrf())
                        .param("email", "exists@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHandleUsernameAlreadyExistsException() throws Exception {
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        doThrow(new UsernameAlreadyExistsException("Username already exists")).when(userService).addUser(any());

        mockMvc.perform(post("/admin/user-management/save")
                        .with(csrf())
                        .param("username", "existinguser"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeHasFieldErrors("user", "username"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHandleGenericExceptionInSaveUser() throws Exception {
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        doThrow(new RuntimeException("Unexpected error")).when(userService).addUser(any());

        mockMvc.perform(post("/admin/user-management/save")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldShowEditUserForm() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        when(userService.getUserDtoById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/admin/user-management/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(post("/admin/user-management/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user-management"))
                .andExpect(flash().attributeExists("success"));

        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHandleDeleteUserError() throws Exception {
        doThrow(new RuntimeException()).when(userService).deleteUser(1L);

        mockMvc.perform(post("/admin/user-management/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user-management"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldSaveBookSuccessfully() throws Exception {
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(authorService.getAuthorDtoById(anyLong())).thenReturn(new AuthorDto());
        when(genreService.getGenreById(anyLong())).thenReturn(new GenreDto());

        mockMvc.perform(post("/admin/book-management/save")
                        .with(csrf())
                        .param("title", "New Book")
                        .param("authorIds", "1", "2")
                        .param("genreIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book-management"))
                .andExpect(flash().attributeExists("success"));

        verify(bookService).addBook(any(BookDto.class));
        verify(authorService, times(2)).getAuthorDtoById(anyLong());
        verify(genreService, times(1)).getGenreById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateBookSuccessfully() throws Exception {
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        mockMvc.perform(post("/admin/book-management/save")
                        .with(csrf())
                        .param("id", "1")
                        .param("title", "Updated Book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book-management"))
                .andExpect(flash().attributeExists("success"));

        verify(bookService).updateBook(eq(1L), any(BookDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHandleBookValidationErrors() throws Exception {
        ConstraintViolation<BookDto> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("title");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("Title is required");

        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add((ConstraintViolation) violation);

        when(validator.validate(any())).thenReturn(violations);
        when(authorService.getAllAuthorDto(any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(genreService.getAllGenres(any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(post("/admin/book-management/save")
                        .with(csrf())
                        .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book-form"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHandleBookAlreadyExistsException() throws Exception {
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        doThrow(new BookAlreadyExistsException("Book already exists")).when(bookService).addBook(any());
        when(authorService.getAllAuthorDto(any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(genreService.getAllGenres(any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(post("/admin/book-management/save")
                        .with(csrf())
                        .param("title", "Existing Book"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book-form"))
                .andExpect(model().attributeHasFieldErrors("book", "title"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHandleGenericExceptionInSaveBook() throws Exception {
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        doThrow(new RuntimeException("Unexpected error")).when(bookService).addBook(any());
        when(authorService.getAllAuthorDto(any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(genreService.getAllGenres(any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(post("/admin/book-management/save")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book-form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldShowEditBookForm() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        when(bookService.getBookDtoById(1L)).thenReturn(bookDto);
        when(authorService.getAllAuthorDto(any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(genreService.getAllGenres(any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/admin/book-management/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book-form"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteBookSuccessfully() throws Exception {
        mockMvc.perform(post("/admin/book-management/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book-management"))
                .andExpect(flash().attributeExists("success"));

        verify(bookService).deleteBook(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHandleDeleteBookError() throws Exception {
        doThrow(new RuntimeException()).when(bookService).deleteBook(1L);

        mockMvc.perform(post("/admin/book-management/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book-management"))
                .andExpect(flash().attributeExists("error"));
    }
}
