package ug.project.library.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ug.project.library.dto.BookDto;
import ug.project.library.dto.ReservationDto;
import ug.project.library.service.BookService;
import ug.project.library.service.ReservationService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReservationViewController.class, properties = "spring.thymeleaf.check-template-location=false")
public class ReservationViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private ReservationService reservationService;

    @Test
    @WithMockUser
    public void shouldReturnReservationPage() throws Exception {
        BookDto book = new BookDto();
        book.setId(1L);
        book.setAuthors(Collections.emptyList());
        when(bookService.getBookDtoById(1L)).thenReturn(book);

        mockMvc.perform(get("/reservation/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void shouldCreateReservation() throws Exception {
        when(reservationService.createNewReservation(1L)).thenReturn(new ReservationDto());

        mockMvc.perform(post("/reservation/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnManageReservationsPage() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/manage-reservations"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldConfirmReservation() throws Exception {
        mockMvc.perform(post("/reservation/1/confirm")
                        .param("source", "admin")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
