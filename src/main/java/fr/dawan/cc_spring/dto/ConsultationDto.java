package fr.dawan.cc_spring.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ConsultationDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String numero;
    @NotNull @Future private LocalDateTime date;
    @NotBlank private String medecinMatricule;
    @NotBlank private String patientNumSS;
}
