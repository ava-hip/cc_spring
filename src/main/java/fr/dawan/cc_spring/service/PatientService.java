package fr.dawan.cc_spring.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.dto.PatientDto;
import fr.dawan.cc_spring.entity.Patient;
import fr.dawan.cc_spring.mapper.DtoMapper;
import fr.dawan.cc_spring.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    private final DtoMapper mapper;

    public PageResponse<PatientDto> rechercher(String search, Pageable pageable) {
        Page<Patient> page;
        if (search == null || search.isBlank()) {
            page = repository.findAll(pageable);
        } else {
            page = repository.findByNomContainingIgnoreCaseOrNumSSContaining(search, search, pageable);
        }
        return mapper.mapPage(page, PatientDto.class);
    }
}
