package fr.dawan.cc_spring.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.dawan.cc_spring.dto.ConsultationDto;
import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.exception.RessourceIntrouvableException;
import fr.dawan.cc_spring.mapper.DtoMapper;
import fr.dawan.cc_spring.repository.ConsultationRepository;
import fr.dawan.cc_spring.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final DtoMapper mapper;

    public PageResponse<ConsultationDto> listerParPatient(String numSS, Pageable pageable) {
        if (!patientRepository.existsById(numSS)) {
            throw new RessourceIntrouvableException("Patient introuvable : " + numSS);
        }
        return mapper.mapPage(consultationRepository.findByPatientNumSS(numSS, pageable), ConsultationDto.class);
    }
}
