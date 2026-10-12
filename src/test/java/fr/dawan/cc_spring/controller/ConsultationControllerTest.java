package fr.dawan.cc_spring.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import fr.dawan.cc_spring.dto.PrescriptionDto;
import fr.dawan.cc_spring.security.SecurityConfig;
import fr.dawan.cc_spring.service.ConsultationService;

@WebMvcTest(ConsultationController.class)
@Import(SecurityConfig.class)
class ConsultationControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ConsultationService consultationService;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void prescriptions_renvoieLesMedicamentsAvecLibelle() throws Exception {
        when(consultationService.detaillerPrescriptions("C1"))
                .thenReturn(List.of(new PrescriptionDto("DOLI500", 3, "Doliprane 500mg")));

        mockMvc.perform(get("/api/consultations/C1/prescriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].libelle").value("Doliprane 500mg"));
    }

    @Test
    @WithMockUser
    void creer_datePassee_renvoie400() throws Exception {
        String json = """
                {"date":"2020-01-01T10:00:00","medecinMatricule":"M001","patientNumSS":"185"}
                """;

        mockMvc.perform(post("/api/consultations").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void supprimer_sansRoleAdmin_renvoie403() throws Exception {
        mockMvc.perform(delete("/api/consultations/C1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void supprimer_admin_renvoie204() throws Exception {
        mockMvc.perform(delete("/api/consultations/C1"))
                .andExpect(status().isNoContent());
        verify(consultationService).supprimer("C1");
    }
}