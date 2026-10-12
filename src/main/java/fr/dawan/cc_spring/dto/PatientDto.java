package fr.dawan.cc_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PatientDto {
    private String numSS;
    private String nom;
}
