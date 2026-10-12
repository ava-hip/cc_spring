package fr.dawan.cc_spring.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.dawan.cc_spring.dto.ConsultationDto;
import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.dto.PatientDto;
import fr.dawan.cc_spring.service.ConsultationService;
import fr.dawan.cc_spring.service.PatientService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final ConsultationService consultationService;

    @GetMapping
    public PageResponse<PatientDto> rechercher(@RequestParam(required = false) String search, Pageable pageable) {
        return patientService.rechercher(search, pageable);
    }

    @GetMapping("/{numSS}/consultations")
    public PageResponse<ConsultationDto> consultations(@PathVariable String numSS, Pageable pageable) {
        return consultationService.listerParPatient(numSS, pageable);
    }
}
