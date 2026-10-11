package fr.dawan.cc_spring.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.dawan.cc_spring.entity.Utilisateur;

public interface UtilisateurRepository extends MongoRepository<Utilisateur, String> {
    Optional<Utilisateur> findByUsername(String username);
}
