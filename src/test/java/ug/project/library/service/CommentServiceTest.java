package ug.project.library.service;

import org.junit.jupiter.api.BeforeEach;
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
import ug.project.library.dto.CommentDto;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Comment;
import ug.project.library.model.entity.User;
import ug.project.library.repository.BookRepository;
import ug.project.library.repository.CommentRepository;
import ug.project.library.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentDto commentDto;
    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        comment = new Comment("Great book!", user, book);
        comment.setId(1L);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("Great book!");
        commentDto.setUserId(1L);
        commentDto.setBookId(1L);
    }

    @Test
    @DisplayName("getAllComments should return page of DTOs")
    void getAllComments_ShouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(commentRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(comment)));

        Page<CommentDto> result = commentService.getAllComments(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Great book!");
    }

    @Test
    @DisplayName("getCommentById should return DTO when found")
    void getCommentById_ShouldReturnDto_WhenFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentDto result = commentService.getCommentById(1L);

        assertThat(result.getContent()).isEqualTo("Great book!");
    }

    @Test
    @DisplayName("getCommentById should throw exception when not found")
    void getCommentById_ShouldThrowException_WhenNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.getCommentById(1L));
    }

    @Test
    @DisplayName("addComment should save and return DTO")
    void addComment_ShouldSaveAndReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.addComment(commentDto);

        assertThat(result.getContent()).isEqualTo("Great book!");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("updateComment should update and return DTO")
    void updateComment_ShouldUpdateAndReturnDto() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        commentDto.setContent("Updated content");
        CommentDto result = commentService.updateComment(1L, commentDto);

        assertThat(result.getContent()).isEqualTo("Updated content");
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("deleteComment should delete when exists")
    void deleteComment_ShouldDelete_WhenExists() {
        when(commentRepository.existsById(1L)).thenReturn(true);

        commentService.deleteComment(1L);

        verify(commentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteComment should throw exception when not found")
    void deleteComment_ShouldThrowException_WhenNotFound() {
        when(commentRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> commentService.deleteComment(1L));
    }
}
