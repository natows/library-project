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
import ug.project.library.model.entity.Reservation;
import ug.project.library.service.AuthService;
import ug.project.library.service.ReservationService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserPanelViewController.class, properties = "spring.thymeleaf.check-template-location=false")
public class UserPanelViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser
    public void shouldReturnMyReservations() throws Exception {
        when(authService.getCurrentUserId()).thenReturn(1L);
        when(reservationService.getUserActiveReservations(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/my-reservations"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/my-reservations"))
                .andExpect(model().attributeExists("reservations"));
    }

    @Test
    @WithMockUser
    public void shouldReturnHistory() throws Exception {
        when(authService.getCurrentUserId()).thenReturn(1L);
        Page<Reservation> historyPage = new PageImpl<>(Collections.emptyList());
        when(reservationService.getUserReservationHistory(anyLong(), any(Pageable.class))).thenReturn(historyPage);

        mockMvc.perform(get("/user/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/history"))
                .andExpect(model().attributeExists("historyPage"))
                .andExpect(model().attribute("currentPage", 0));
    }
}
