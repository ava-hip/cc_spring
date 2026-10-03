package fr.dawan.cc_spring.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("medecin")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor  
public class Medecin {
    @Id 
    private String matricule;
    @Indexed 
    private String nom;
}
