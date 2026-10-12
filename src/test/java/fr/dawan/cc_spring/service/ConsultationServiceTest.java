package fr.dawan.cc_spring.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import fr.dawan.cc_spring.exception.RessourceIntrouvableException;
import fr.dawan.cc_spring.mapper.DtoMapper;
import fr.dawan.cc_spring.repository.ConsultationRepository;
import fr.dawan.cc_spring.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
public class ConsultationServiceTest {
    @Mock private ConsultationRepository consultationRepository;
    @Mock private PatientRepository patientRepository;
    private ConsultationService service;

    @BeforeEach
    void setUp() {
        service = new ConsultationService(consultationRepository, patientRepository, new DtoMapper());
    }

    @Test
    void listerParPatient_patientInconnu_leveException() {
        when(patientRepository.existsById("000")).thenReturn(false);

        Pageable pageable = PageRequest.of(0, 20);

        assertThatThrownBy(() -> service.listerParPatient("000", pageable))
                .isInstanceOf(RessourceIntrouvableException.class);
    }
}
