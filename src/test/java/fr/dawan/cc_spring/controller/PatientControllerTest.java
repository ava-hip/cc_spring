package fr.dawan.cc_spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.dto.PatientDto;
import fr.dawan.cc_spring.service.ConsultationService;
import fr.dawan.cc_spring.service.PatientService;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private PatientService patientService;
    @MockitoBean private ConsultationService consultationService;

        @Test
    @WithMockUser
    void rechercher_renvoieLaPageEnJson() throws Exception {
        when(patientService.rechercher(eq("dup"), any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(new PatientDto("185", "Dupont")), 0, 20, 1, 1));

        mockMvc.perform(get("/api/patients").param("search", "dup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nom").value("Dupont"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
