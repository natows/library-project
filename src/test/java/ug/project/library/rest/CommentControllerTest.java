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
import ug.project.library.controller.CommentController;
import ug.project.library.dto.CommentDto;
import ug.project.library.service.CommentService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void shouldReturnAllComments() throws Exception {
        CommentDto comment = createSampleCommentDto(1L, "Nice book");
        PageRequest pageable = PageRequest.of(0, 10);
        when(commentService.getAllComments(any())).thenReturn(new PageImpl<>(Collections.singletonList(comment), pageable, 1));

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Nice book"));
    }

    @Test
    @WithMockUser
    public void shouldReturnCommentById() throws Exception {
        CommentDto comment = createSampleCommentDto(1L, "Nice book");
        when(commentService.getCommentById(1L)).thenReturn(comment);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Nice book"));
    }

    @Test
    @WithMockUser
    public void shouldAddComment() throws Exception {
        CommentDto comment = createSampleCommentDto(null, "New Comment");
        CommentDto savedComment = createSampleCommentDto(1L, "New Comment");
        when(commentService.addComment(any(CommentDto.class))).thenReturn(savedComment);

        mockMvc.perform(post("/api/comments/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("New Comment"));
    }

    @Test
    @WithMockUser
    public void shouldUpdateComment() throws Exception {
        CommentDto comment = createSampleCommentDto(1L, "Updated Comment");
        when(commentService.updateComment(eq(1L), any(CommentDto.class))).thenReturn(comment);

        mockMvc.perform(put("/api/comments/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated Comment"));
    }

    @Test
    @WithMockUser
    public void shouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/comments/delete/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    private CommentDto createSampleCommentDto(Long id, String content) {
        return new CommentDto(id, content, LocalDateTime.now(), LocalDateTime.now(), 1L, "user", 1L, "Title");
    }
}
