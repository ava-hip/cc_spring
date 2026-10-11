package fr.dawan.cc_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fr.dawan.cc_spring.entity.Patient;

public interface PatientRepository extends MongoRepository<Patient, String> {
    Page<Patient> findByNomContainingIgnoreCaseOrNumSSContaining(String nom, String numSS, Pageable pageable);
}
