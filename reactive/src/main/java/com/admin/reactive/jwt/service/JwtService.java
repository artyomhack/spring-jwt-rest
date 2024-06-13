package com.admin.reactive.jwt.service;

import com.admin.reactive.entities.User;
import com.admin.reactive.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;

    @Value("${jwt.token.secret}")
    private String secret;
    @Value("${jwt.token.lifeTime}")
    private Duration jwtExpiration;
    private SecretKey secretKey;

    public String generatedJwt(Authentication authentication) {
        Map<String, Object> claims = Map.of(
                "username", authentication.getName(),
                "roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration.toMillis()))
                .setSubject(authentication.getName())
                .signWith(generateSecretKey())
                .compact();
    }

    public Claims getClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(generateSecretKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String getUsername(String jwt) {
        return getClaims(jwt).getSubject();
    }

    public List<? extends  GrantedAuthority> getAuthorities(String jwt) {
        if (getClaims(jwt).get("roles") != null && getClaims(jwt).get("roles") instanceof List<?> roles) {
            log.debug("Get roles from JwtService");
            return roles.stream().map(it -> new SimpleGrantedAuthority(it.toString())).toList();
        }
        log.debug("List is empty");
        return Collections.emptyList();
    }

    public boolean isValidJwt(String jwt) {
        Claims claims = getClaims(jwt);
        Optional<User> user = userRepository.findByUsername(String.valueOf(claims.get("username")));
        return claims.getExpiration().after(new Date()) && user.isPresent();
    }

    private SecretKey generateSecretKey() {
        if (secretKey == null) {
            byte[] bytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
            secretKey = Keys.hmacShaKeyFor(bytes);
        }
        return secretKey;
    }
}
