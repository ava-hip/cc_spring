package fr.dawan.cc_spring.service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import fr.dawan.cc_spring.dto.LoginRequest;
import fr.dawan.cc_spring.dto.TokenResponse;
import lombok.RequiredArgsConstructor;

/** Authentifie un utilisateur et lui délivre un jeton JWT. */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final Clock clock;

    public TokenResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Instant maintenant = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cc_spring")
                .subject(auth.getName())
                .issuedAt(maintenant)
                .expiresAt(maintenant.plus(1, ChronoUnit.HOURS))
                .claim("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse(token);
    }
}
