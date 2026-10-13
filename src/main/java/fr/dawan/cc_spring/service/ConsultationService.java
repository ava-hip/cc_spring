package fr.dawan.cc_spring.service;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.dawan.cc_spring.dto.ConsultationDto;
import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.dto.PrescriptionDto;
import fr.dawan.cc_spring.entity.Consultation;
import fr.dawan.cc_spring.entity.Medicament;
import fr.dawan.cc_spring.entity.Prescription;
import fr.dawan.cc_spring.exception.ConsultationNonModifiableException;
import fr.dawan.cc_spring.exception.RessourceIntrouvableException;
import fr.dawan.cc_spring.mapper.DtoMapper;
import fr.dawan.cc_spring.repository.ConsultationRepository;
import fr.dawan.cc_spring.repository.MedecinRepository;
import fr.dawan.cc_spring.repository.MedicamentRepository;
import fr.dawan.cc_spring.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final MedicamentRepository medicamentRepository;
    private final DtoMapper mapper;
    private final Clock clock;

    public PageResponse<ConsultationDto> listerParPatient(String numSS, Pageable pageable) {
        verifierPatient(numSS);
        return mapper.mapPage(consultationRepository.findByPatientNumSS(numSS, pageable), ConsultationDto.class);
    }

    public List<PrescriptionDto> detaillerPrescriptions(String numero) {
        return detailler(trouver(numero));
    }

    public ConsultationDto creer(ConsultationDto dto) {
        verifierMedecin(dto.getMedecinMatricule());
        verifierPatient(dto.getPatientNumSS());
        Consultation consultation = new Consultation(dto.getDate(), dto.getMedecinMatricule(), dto.getPatientNumSS());
        return mapper.map(consultationRepository.save(consultation), ConsultationDto.class);
    }

    public ConsultationDto replanifier(String numero, ConsultationDto dto) {
        Consultation consultation = trouverModifiable(numero);
        verifierMedecin(dto.getMedecinMatricule());
        consultation.replanifier(dto.getDate(), dto.getMedecinMatricule());
        return mapper.map(consultationRepository.save(consultation), ConsultationDto.class);
    }

    public void supprimer(String numero) {
        consultationRepository.delete(trouverModifiable(numero));
    }

    public List<PrescriptionDto> modifierPrescriptions(String numero, List<PrescriptionDto> dtos) {
        Consultation consultation = trouverModifiable(numero);
        verifierMedicaments(dtos);
        consultation.remplacerPrescriptions(mapper.mapList(dtos, Prescription.class));
        return detailler(consultationRepository.save(consultation));
    }

    private List<PrescriptionDto> detailler(Consultation consultation) {
        List<String> codes = consultation.getPrescriptions().stream()
                .map(Prescription::getCodeMedicament).toList();
        Map<String, String> libelles = medicamentRepository.findAllById(codes).stream()
                .collect(Collectors.toMap(Medicament::getCode, Medicament::getLibelle));
        return consultation.getPrescriptions().stream().map(p -> {
            PrescriptionDto dto = mapper.map(p, PrescriptionDto.class);
            dto.setLibelle(libelles.get(p.getCodeMedicament()));
            return dto;
        }).toList();
    }

    private Consultation trouver(String numero) {
        return consultationRepository.findById(numero)
                .orElseThrow(() -> new RessourceIntrouvableException("Consultation introuvable : " + numero));
    }

    private Consultation trouverModifiable(String numero) {
        Consultation consultation = trouver(numero);
        if (!consultation.estProgrammee(clock)) {
            throw new ConsultationNonModifiableException(numero);
        }
        return consultation;
    }

    private void verifierPatient(String numSS) {
        if (!patientRepository.existsById(numSS)) {
            throw new RessourceIntrouvableException("Patient introuvable : " + numSS);
        }
    }

    private void verifierMedecin(String matricule) {
        if (!medecinRepository.existsById(matricule)) {
            throw new RessourceIntrouvableException("Médecin introuvable : " + matricule);
        }
    }

    private void verifierMedicaments(List<PrescriptionDto> dtos) {
        Set<String> codes = dtos.stream().map(PrescriptionDto::getCodeMedicament).collect(Collectors.toSet());
        Set<String> existants = medicamentRepository.findAllById(codes).stream()
                .map(Medicament::getCode).collect(Collectors.toSet());
        List<String> inconnus = codes.stream().filter(c -> !existants.contains(c)).toList();
        if (!inconnus.isEmpty()) {
            throw new RessourceIntrouvableException("Médicament(s) introuvable(s) : " + inconnus);
        }
    }

    public PageResponse<ConsultationDto> listerParMedecin(String matricule, Pageable pageable) {
        verifierMedecin(matricule);
        return mapper.mapPage(consultationRepository.findByMedecinMatricule(matricule, pageable), ConsultationDto.class);
    }
}