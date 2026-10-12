package fr.dawan.cc_spring.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.dawan.cc_spring.dto.MedecinDto;
import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.service.MedecinService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medecin")
@RequiredArgsConstructor
public class MedecinController {
    private final MedecinService medecinService;

    @GetMapping
    public PageResponse<MedecinDto> rechercher(@RequestParam(required = false) String search, Pageable pageable) {
        return medecinService.rechercher(search, pageable);
    }
}
