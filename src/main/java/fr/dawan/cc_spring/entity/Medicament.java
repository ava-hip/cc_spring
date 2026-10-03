package fr.dawan.cc_spring.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("medicament")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor  
public class Medicament {
    @Id 
    private String code;
    @Indexed 
    private String libelle;
}
