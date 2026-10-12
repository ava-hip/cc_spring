package fr.dawan.cc_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PrescriptionDto {
    @NotBlank private String codeMedicament;
    @Positive private int nbPrises;
    private String libelle;
}
