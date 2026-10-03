package fr.dawan.cc_spring.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Document("consultation")
@Getter @NoArgsConstructor 
public class Consultation {
    @Id 
    private String numero;
    private LocalDateTime date;
    @Indexed 
    private String medecinMatricule;
    @Indexed
    private String patientNumSS;
    private List<Prescription> prescriptions = new ArrayList<>();

    public Consultation(LocalDateTime date, String medecinMatricule, String patientNumSS) {
        this.date = date;
        this.medecinMatricule = medecinMatricule;
        this.patientNumSS = patientNumSS;
    }

    public boolean estProgrammee() {
        return date.isAfter(LocalDateTime.now());
    }

    public void remplacerPrescriptions(List<Prescription> nouvelles) {
        this.prescriptions = new ArrayList<>(nouvelles);
    }

    public void replanifier(LocalDateTime nouvelleDate) {
        this.date = nouvelleDate;
    }
}
