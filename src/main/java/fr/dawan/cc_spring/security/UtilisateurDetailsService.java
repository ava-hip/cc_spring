package fr.dawan.cc_spring.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.dawan.cc_spring.entity.Utilisateur;
import fr.dawan.cc_spring.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UtilisateurDetailsService implements UserDetailsService{
    private final UtilisateurRepository repository;

    @Override 
    public UserDetails loadUserByUsername(String username) {
        Utilisateur u = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRoles().stream().map(Enum::name).toArray(String[]::new))
                   .build();
    }
}
