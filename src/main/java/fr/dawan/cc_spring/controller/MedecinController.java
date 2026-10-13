package fr.dawan.cc_spring.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.dawan.cc_spring.dto.ConsultationDto;
import fr.dawan.cc_spring.dto.MedecinDto;
import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.service.ConsultationService;
import fr.dawan.cc_spring.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@Tag(name = "Médecins")
@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
public class MedecinController {
    private final MedecinService medecinService;
    private final ConsultationService consultationService;

    @Operation(summary = "Liste paginée des médecins, recherche optionnelle sur le nom ou le matricule")
    @GetMapping
    public PageResponse<MedecinDto> rechercher(@RequestParam(required = false) String search, Pageable pageable) {
        return medecinService.rechercher(search, pageable);
    }

    @Operation(summary = "Liste paginée des consultations d'un médecin")
    @GetMapping("/{matricule}/consultations")
    public PageResponse<ConsultationDto> consultations(@PathVariable String matricule, Pageable pageable) {
        return consultationService.listerParMedecin(matricule, pageable);
    }
}
