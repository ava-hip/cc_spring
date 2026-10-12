package fr.dawan.cc_spring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor @AllArgsConstructor
public class Prescription {
    private String codeMedicament;
    private int nbPrises;
}
