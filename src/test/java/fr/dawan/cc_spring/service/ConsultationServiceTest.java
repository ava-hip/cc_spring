package fr.dawan.cc_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import fr.dawan.cc_spring.dto.ConsultationDto;
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

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock private ConsultationRepository consultationRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private MedecinRepository medecinRepository;
    @Mock private MedicamentRepository medicamentRepository;

    private final Clock clock = Clock.fixed(Instant.parse("2026-10-12T10:00:00Z"), ZoneId.of("Europe/Paris"));
    private final LocalDateTime maintenant = LocalDateTime.now(clock);

    private ConsultationService service;

    @BeforeEach
    void setUp() {
        service = new ConsultationService(consultationRepository, patientRepository, medecinRepository,
                medicamentRepository, new DtoMapper(), clock);
    }

    @Test
    void listerParPatient_patientInconnu_leveException() {
        when(patientRepository.existsById("000")).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 20);

        assertThatThrownBy(() -> service.listerParPatient("000", pageable))
                .isInstanceOf(RessourceIntrouvableException.class);
    }

    @Test
    void detaillerPrescriptions_ajouteLeLibelleDuMedicament() {
        Consultation consultation = new Consultation(maintenant.plusDays(1), "M001", "185");
        consultation.remplacerPrescriptions(List.of(new Prescription("DOLI500", 3)));
        when(consultationRepository.findById("C1")).thenReturn(Optional.of(consultation));
        when(medicamentRepository.findAllById(List.of("DOLI500")))
                .thenReturn(List.of(new Medicament("DOLI500", "Doliprane 500mg")));

        List<PrescriptionDto> resultat = service.detaillerPrescriptions("C1");

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getCodeMedicament()).isEqualTo("DOLI500");
        assertThat(resultat.get(0).getNbPrises()).isEqualTo(3);
        assertThat(resultat.get(0).getLibelle()).isEqualTo("Doliprane 500mg");
    }

    @Test
    void creer_enregistreLaConsultation() {
        when(medecinRepository.existsById("M001")).thenReturn(true);
        when(patientRepository.existsById("185")).thenReturn(true);
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(inv -> inv.getArgument(0));

        ConsultationDto resultat = service.creer(new ConsultationDto(null, maintenant.plusDays(3), "M001", "185"));

        assertThat(resultat.getMedecinMatricule()).isEqualTo("M001");
        assertThat(resultat.getPatientNumSS()).isEqualTo("185");
        assertThat(resultat.getDate()).isEqualTo(maintenant.plusDays(3));
    }

    @Test
    void creer_medecinInconnu_leveException() {
        when(medecinRepository.existsById("X")).thenReturn(false);
        ConsultationDto dto = new ConsultationDto(null, maintenant.plusDays(3), "X", "185");

        assertThatThrownBy(() -> service.creer(dto)).isInstanceOf(RessourceIntrouvableException.class);
        verify(consultationRepository, never()).save(any());
    }

    @Test
    void replanifier_consultationPassee_leveException() {
        Consultation passee = new Consultation(maintenant.minusDays(1), "M001", "185");
        when(consultationRepository.findById("C1")).thenReturn(Optional.of(passee));
        ConsultationDto dto = new ConsultationDto(null, maintenant.plusDays(5), "M001", "185");

        assertThatThrownBy(() -> service.replanifier("C1", dto))
                .isInstanceOf(ConsultationNonModifiableException.class);
    }

    @Test
    void supprimer_consultationFuture_supprime() {
        Consultation future = new Consultation(maintenant.plusDays(1), "M001", "185");
        when(consultationRepository.findById("C1")).thenReturn(Optional.of(future));

        service.supprimer("C1");

        verify(consultationRepository).delete(future);
    }

    @Test
    void modifierPrescriptions_medicamentInconnu_leveException() {
        Consultation future = new Consultation(maintenant.plusDays(1), "M001", "185");
        when(consultationRepository.findById("C1")).thenReturn(Optional.of(future));
        when(medicamentRepository.findAllById(any())).thenReturn(List.of());
        List<PrescriptionDto> prescriptions = List.of(new PrescriptionDto("XXX", 2, null));

        assertThatThrownBy(() -> service.modifierPrescriptions("C1", prescriptions))
                .isInstanceOf(RessourceIntrouvableException.class);
    }

    @Test
    void listerParMedecin_medecinInconnu_leveException() {
        when(medecinRepository.existsById("X")).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 20);

        assertThatThrownBy(() -> service.listerParMedecin("X", pageable))
                .isInstanceOf(RessourceIntrouvableException.class);
    }
}