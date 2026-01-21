package ug.project.library.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ug.project.library.dto.*;
import ug.project.library.exceptions.*;
import ug.project.library.model.enumerate.UserRole;

import java.util.List;
import java.util.stream.Collectors;

import ug.project.library.service.AuthorService;
import ug.project.library.service.BookService;
import ug.project.library.service.GenreService;
import ug.project.library.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final jakarta.validation.Validator validator;
    private final UserService userService;

    public AdminViewController(BookService bookService, AuthorService authorService, GenreService genreService, jakarta.validation.Validator validator, UserService userService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
        this.validator = validator;
        this.userService = userService;
    }

    @GetMapping("/book-management")
    public String listBooks(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<BookDto> books = bookService.getAllBooksDto(pageable);
        model.addAttribute("books", books);
        return "admin/book-management";
    }

    @GetMapping("/book-management/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new BookDto());
        model.addAttribute("authors", authorService.getAllAuthorDto(Pageable.unpaged()).getContent());
        model.addAttribute("genres", genreService.getAllGenres(Pageable.unpaged()).getContent());
        return "admin/book-form";
    }

    @GetMapping("/book-management/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BookDto bookDto = bookService.getBookDtoById(id);
        model.addAttribute("book", bookDto);
        model.addAttribute("authors", authorService.getAllAuthorDto(Pageable.unpaged()).getContent());
        model.addAttribute("genres", genreService.getAllGenres(Pageable.unpaged()).getContent());
        return "admin/book-form";
    }

    @PostMapping("/book-management/save")
    public String saveBook(@ModelAttribute("book") BookDto bookDto, BindingResult bindingResult, 
                           @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                           @RequestParam(value = "genreIds", required = false) List<Long> genreIds,
                           Model model, RedirectAttributes redirectAttributes) {
        if (authorIds != null) {
            bookDto.setAuthors(authorIds.stream()
                .map(authorService::getAuthorDtoById)
                .collect(Collectors.toList()));
        }
        if (genreIds != null) {
            bookDto.setGenres(genreIds.stream()
                .map(genreService::getGenreById)
                .collect(Collectors.toList()));
        }

        validator.validate(bookDto).forEach(violation ->
            bindingResult.rejectValue(violation.getPropertyPath().toString(), "error", violation.getMessage())
        );

        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.getAllAuthorDto(Pageable.unpaged()).getContent());
            model.addAttribute("genres", genreService.getAllGenres(Pageable.unpaged()).getContent());
            return "admin/book-form";
        }

        try {
            if (bookDto.getId() == null) {
                bookService.addBook(bookDto);
                redirectAttributes.addFlashAttribute("success", "Książka została dodana pomyślnie.");
            } else {
                bookService.updateBook(bookDto.getId(), bookDto);
                redirectAttributes.addFlashAttribute("success", "Książka została zaktualizowana pomyślnie.");
            }
        } catch (BookAlreadyExistsException e) {
            bindingResult.rejectValue("title", "error", e.getMessage());
            model.addAttribute("authors", authorService.getAllAuthorDto(Pageable.unpaged()).getContent());
            model.addAttribute("genres", genreService.getAllGenres(Pageable.unpaged()).getContent());
            return "admin/book-form";
        } catch (Exception e) {
            model.addAttribute("error", "Wystąpił nieoczekiwany błąd: " + e.getMessage());
            model.addAttribute("authors", authorService.getAllAuthorDto(Pageable.unpaged()).getContent());
            model.addAttribute("genres", genreService.getAllGenres(Pageable.unpaged()).getContent());
            return "admin/book-form";
        }

        return "redirect:/admin/book-management";
    }

    @PostMapping("/book-management/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Książka została usunięta.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nie udało się usunąć książki.");
        }
        return "redirect:/admin/book-management";
    }


    @GetMapping("/user-management")
    public String listUsers(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<UserDto> users = userService.getAllUsers(pageable);
        model.addAttribute("users", users);
        return "admin/user-management";
    }

    @GetMapping("/user-management/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("roles", UserRole.values());
        return "admin/user-form";
    }

    @GetMapping("/user-management/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        UserDto userDto = userService.getUserDtoById(id);
        model.addAttribute("user", userDto);
        model.addAttribute("roles", UserRole.values());
        return "admin/user-form";
    }

    @PostMapping("/user-management/save")
    public String saveUser(@ModelAttribute("user") UserDto userDto, BindingResult bindingResult,
                           Model model, RedirectAttributes redirectAttributes) {

        validator.validate(userDto).forEach(violation ->
            bindingResult.rejectValue(violation.getPropertyPath().toString(), "error", violation.getMessage())
        );

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            return "admin/user-form";
        }

        try {
            if (userDto.getId() == null) {
                userService.addUser(userDto);
                redirectAttributes.addFlashAttribute("success", "User added successfully.");
            } else {
                userService.updateUser(userDto.getId(), userDto);
                redirectAttributes.addFlashAttribute("success", "User updated successfully.");
            }
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "error", e.getMessage());
            model.addAttribute("roles", UserRole.values());
            return "admin/user-form";
        } catch (UsernameAlreadyExistsException e) {
            bindingResult.rejectValue("username", "error", e.getMessage());
            model.addAttribute("roles", UserRole.values());
            return "admin/user-form";
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("roles", UserRole.values());
            return "admin/user-form";
        }

        return "redirect:/admin/user-management";
    }

    @PostMapping("/user-management/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user.");
        }
        return "redirect:/admin/user-management";
    }




}
