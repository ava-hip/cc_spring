package fr.dawan.cc_spring.entity;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document("utilisateur")
@Getter @NoArgsConstructor @AllArgsConstructor
public class Utilisateur {
    @Id private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private Set<Role> roles;
}
