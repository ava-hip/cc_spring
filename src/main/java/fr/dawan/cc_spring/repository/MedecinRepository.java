package fr.dawan.cc_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fr.dawan.cc_spring.entity.Medecin;

public interface MedecinRepository extends MongoRepository<Medecin, String> {
    Page<Medecin> findByNomContainingIgnoreCaseOrMatriculeContaining(String nom, String matricule, Pageable pageable);
}
