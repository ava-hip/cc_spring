package fr.dawan.cc_spring.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("patient") 
@Getter @Setter @NoArgsConstructor @AllArgsConstructor 
public class Patient {
    @Id 
    private String numSS;
    @Indexed 
    private String nom;
}
