package fr.dawan.cc_spring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.dawan.cc_spring.entity.Medicament;

public interface MedicamentRepository extends MongoRepository<Medicament, String>{   
}
