package fr.dawan.cc_spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import fr.dawan.cc_spring.dto.ConsultationDto;
import fr.dawan.cc_spring.dto.MedecinDto;
import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.exception.RessourceIntrouvableException;
import fr.dawan.cc_spring.security.SecurityConfig;
import fr.dawan.cc_spring.service.ConsultationService;
import fr.dawan.cc_spring.service.MedecinService;

@WebMvcTest(MedecinController.class)
@Import(SecurityConfig.class)
class MedecinControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private MedecinService medecinService;
    @MockitoBean private ConsultationService consultationService;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void rechercher_renvoieLaPageEnJson() throws Exception {
        when(medecinService.rechercher(eq("dur"), any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(new MedecinDto("M001", "Durand")), 0, 20, 1, 1));

        mockMvc.perform(get("/api/medecins").param("search", "dur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].matricule").value("M001"));
    }

    @Test
    @WithMockUser
    void consultations_renvoieLesConsultationsDuMedecin() throws Exception {
        ConsultationDto c = new ConsultationDto("C1", LocalDateTime.of(2030, 1, 15, 9, 0), "M001", "185");
        when(consultationService.listerParMedecin(eq("M001"), any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(c), 0, 20, 1, 1));

        mockMvc.perform(get("/api/medecins/M001/consultations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].numero").value("C1"))
                .andExpect(jsonPath("$.content[0].medecinMatricule").value("M001"));
    }

    @Test
    @WithMockUser
    void consultations_medecinInconnu_renvoie404() throws Exception {
        when(consultationService.listerParMedecin(eq("X"), any(Pageable.class)))
                .thenThrow(new RessourceIntrouvableException("Médecin introuvable : X"));

        mockMvc.perform(get("/api/medecins/X/consultations"))
                .andExpect(status().isNotFound());
    }
}