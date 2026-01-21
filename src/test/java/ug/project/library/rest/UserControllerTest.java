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
import ug.project.library.controller.UserController;
import ug.project.library.dto.UserDto;
import ug.project.library.model.enumerate.UserRole;
import ug.project.library.service.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnAllUsers() throws Exception {
        UserDto user = new UserDto(1L, "user", "user@example.com", UserRole.USER);
        PageRequest pageable = PageRequest.of(0, 10);
        when(userService.getAllUsers(any())).thenReturn(new PageImpl<>(Collections.singletonList(user), pageable, 1));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnUserById() throws Exception {
        UserDto user = new UserDto(1L, "user", "user@example.com", UserRole.USER);
        when(userService.getUserDtoById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateUser() throws Exception {
        UserDto user = new UserDto(1L, "updated", "user@example.com", UserRole.USER);
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/delete/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
