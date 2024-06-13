package com.admin.reactive.jwt.filter;

import com.admin.reactive.dto.user.UserRequest;
import com.admin.reactive.jwt.service.JwtService;
import com.admin.reactive.provider.UsernamePasswordAuthenticationProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsernamePasswordAuthenticationProvider provider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader("Authorization") == null) {
            String bodyJson = request.getReader().lines().collect(Collectors.joining());
            if (!bodyJson.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                log.info("Info a bodyJson in doFilterInternal: {}", bodyJson);
                UserRequest user = mapper.readValue(bodyJson, UserRequest.class);
                String username = user.getUsername();
                String password = user.getPassword();
                try {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
                    authentication = provider.authenticate(authentication);
                    String jwt = jwtService.generatedJwt(authentication);
                    response.setHeader("Authorization", "Bearer " + jwt);
                } catch (BadCredentialsException e) {
                    log.error("Catch exception: {}", e);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/login");
    }
}
