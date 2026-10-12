package fr.dawan.cc_spring.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.dawan.cc_spring.dto.MedecinDto;
import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.entity.Medecin;
import fr.dawan.cc_spring.mapper.DtoMapper;
import fr.dawan.cc_spring.repository.MedecinRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class MedecinService {
    private final MedecinRepository repository;
    private final DtoMapper mapper;

    public PageResponse<MedecinDto> rechercher(String search, Pageable pageable) {
        Page<Medecin> page;
        if (search == null || search.isBlank()) {
            page = repository.findAll(pageable);
        } else {
            page = repository.findByNomContainingIgnoreCaseOrMatriculeContaining(search, search, pageable);
        }
        return mapper.mapPage(page, MedecinDto.class);
    }
}
