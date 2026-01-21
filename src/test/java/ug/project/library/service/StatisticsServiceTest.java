package ug.project.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.User;
import ug.project.library.repository.AuthorRepository;
import ug.project.library.repository.BookRepository;
import ug.project.library.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    @DisplayName("getMostPopularBooks should call repository")
    void getMostPopularBooks_ShouldCallRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(new Book()));
        when(bookRepository.findMostPopularBooks(pageable)).thenReturn(bookPage);

        Page<Book> result = statisticsService.getMostPopularBooks(pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("getUsersWithMostLoans should call repository")
    void getUsersWithMostLoans_ShouldCallRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(new User()));
        when(userRepository.getUsersWithMostLoans(pageable)).thenReturn(userPage);

        Page<User> result = statisticsService.getUsersWithMostLoans(pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("getMostPopularAuthors should call repository")
    void getMostPopularAuthors_ShouldCallRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Author> authorPage = new PageImpl<>(List.of(new Author()));
        when(authorRepository.findMostPopularAuthors(pageable)).thenReturn(authorPage);

        Page<Author> result = statisticsService.getMostPopularAuthors(pageable);

        assertThat(result.getContent()).hasSize(1);
    }
}
