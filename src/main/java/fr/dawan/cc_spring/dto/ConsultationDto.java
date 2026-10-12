package fr.dawan.cc_spring.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ConsultationDto {
    private String numero;
    private LocalDateTime date;
    private String medecinMatricule;
    private String patientNumSS;
}
