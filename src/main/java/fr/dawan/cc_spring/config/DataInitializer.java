package fr.dawan.cc_spring.config;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import fr.dawan.cc_spring.entity.Consultation;
import fr.dawan.cc_spring.entity.Medecin;
import fr.dawan.cc_spring.entity.Medicament;
import fr.dawan.cc_spring.entity.Patient;
import fr.dawan.cc_spring.entity.Prescription;
import fr.dawan.cc_spring.entity.Role;
import fr.dawan.cc_spring.entity.Utilisateur;
import fr.dawan.cc_spring.repository.ConsultationRepository;
import fr.dawan.cc_spring.repository.MedecinRepository;
import fr.dawan.cc_spring.repository.MedicamentRepository;
import fr.dawan.cc_spring.repository.PatientRepository;
import fr.dawan.cc_spring.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final MedicamentRepository medicamentRepository;
    private final ConsultationRepository consultationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    public void run(String... args) {
        
        if (utilisateurRepository.count() == 0) {
            utilisateurRepository.saveAll(List.of(
                new Utilisateur(null, "admin", passwordEncoder.encode("admin123"), Set.of(Role.ADMIN)),
                new Utilisateur(null, "medecin", passwordEncoder.encode("medecin123"), Set.of(Role.MEDECIN))));
        }

        if (patientRepository.count() > 0) return;

        patientRepository.saveAll(List.of(
            new Patient("185057512345678", "Dupont"),
            new Patient("290036912345678", "Martin")));
        medecinRepository.saveAll(List.of(
            new Medecin("M001", "Durand"),
            new Medecin("M002", "Bernard")));
        medicamentRepository.saveAll(List.of(
            new Medicament("DOLI500", "Doliprane 500mg"),
            new Medicament("AMOX1G", "Amoxicilline 1g")));

        Consultation c = new Consultation(LocalDateTime.now(clock).plusDays(7), "M001", "185057512345678");
        c.remplacerPrescriptions(List.of(new Prescription("DOLI500", 3)));
        consultationRepository.save(c);
    }
}
