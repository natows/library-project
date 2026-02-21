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
import ug.project.library.dto.CommentDto;
import ug.project.library.service.*;

import jakarta.validation.Validator;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminViewController.class, properties = "spring.thymeleaf.check-template-location=false")
public class AdminCommentControllerTest {

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
    public void shouldListCommentsForAdmin() throws Exception {
        Page<CommentDto> commentsPage = new PageImpl<>(Collections.emptyList());
        when(commentService.getAllComments(any(Pageable.class))).thenReturn(commentsPage);

        mockMvc.perform(get("/admin/comment-management"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/comment-management"))
                .andExpect(model().attributeExists("comments"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldShowEditCommentForm() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        when(commentService.getCommentById(1L)).thenReturn(commentDto);

        mockMvc.perform(get("/admin/comment-management/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/comment-form"))
                .andExpect(model().attributeExists("comment"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldSaveCommentSuccessfully() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("Updated content");

        when(validator.validate(any())).thenReturn(Collections.emptySet());

        mockMvc.perform(post("/admin/comment-management/save")
                        .with(csrf())
                        .flashAttr("comment", commentDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comment-management"))
                .andExpect(flash().attributeExists("success"));

        verify(commentService).updateComment(eq(1L), any(CommentDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteCommentSuccessfully() throws Exception {
        mockMvc.perform(post("/admin/comment-management/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comment-management"))
                .andExpect(flash().attributeExists("success"));

        verify(commentService).deleteComment(1L);
    }
}
