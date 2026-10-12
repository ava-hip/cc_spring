package fr.dawan.cc_spring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.dawan.cc_spring.dto.ConsultationDto;
import fr.dawan.cc_spring.dto.PrescriptionDto;
import fr.dawan.cc_spring.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Consultations")
@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @Operation(summary = "Détaille les médicaments prescrits dans une consultation")
    @GetMapping("/{numero}/prescriptions")
    public List<PrescriptionDto> prescriptions(@PathVariable String numero) {
        return consultationService.detaillerPrescriptions(numero);
    }

    @Operation(summary = "Programme une nouvelle consultation (date future)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultationDto creer(@Valid @RequestBody ConsultationDto dto) {
        return consultationService.creer(dto);
    }

    @Operation(summary = "Modifie la date et le médecin d'une consultation programmée")
    @PutMapping("/{numero}")
    public ConsultationDto replanifier(@PathVariable String numero, @Valid @RequestBody ConsultationDto dto) {
        return consultationService.replanifier(numero, dto);
    }

    @Operation(summary = "Supprime une consultation programmée (administrateur)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{numero}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable String numero) {
        consultationService.supprimer(numero);
    }

    @Operation(summary = "Remplace la prescription d'une consultation programmée")
    @PutMapping("/{numero}/prescriptions")
    public List<PrescriptionDto> modifierPrescriptions(@PathVariable String numero,
                                                       @RequestBody List<@Valid PrescriptionDto> prescriptions) {
        return consultationService.modifierPrescriptions(numero, prescriptions);
    }
}