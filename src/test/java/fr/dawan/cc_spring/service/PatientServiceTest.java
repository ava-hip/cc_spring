package fr.dawan.cc_spring.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import fr.dawan.cc_spring.dto.PageResponse;
import fr.dawan.cc_spring.dto.PatientDto;
import fr.dawan.cc_spring.entity.Patient;
import fr.dawan.cc_spring.mapper.DtoMapper;
import fr.dawan.cc_spring.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @Mock private PatientRepository repository;
    private PatientService service;
    private final Pageable pageable = PageRequest.of(0, 20);
    
    @BeforeEach
    void setUp() {
        service = new PatientService(repository, new DtoMapper());
    }

    @Test 
    void rechercher_vide_listTousLesPatients() {
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new Patient("185", "Dupont")), pageable, 1));
    
        PageResponse<PatientDto> resultat = service.rechercher(null, pageable);

        assertThat(resultat.getTotalElements()).isEqualTo(1);
        assertThat(resultat.getContent().get(0).getNom()).isEqualTo("Dupont");
        assertThat(resultat.getContent().get(0).getNumSS()).isEqualTo("185");
    }

    @Test
    void rechercher_avecTerme_utiliseLaRecherche() {
        when(repository.findByNomContainingIgnoreCaseOrNumSSContaining("dup", "dup", pageable))
                .thenReturn(Page.empty(pageable));

        service.rechercher("dup", pageable);

        verify(repository).findByNomContainingIgnoreCaseOrNumSSContaining("dup", "dup", pageable);
        verify(repository, never()).findAll(any(Pageable.class));
    }
}
