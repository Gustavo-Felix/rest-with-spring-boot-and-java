package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.dto.security.AccountCredentialsDTO;
import com.gustavofelix.rest_spring_boot.dto.security.TokenDTO;
import com.gustavofelix.rest_spring_boot.repository.UserRepository;
import com.gustavofelix.rest_spring_boot.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO credentials) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        credentials.getPassword()
                )
        );

        var user = userRepository.findByUsername(credentials.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + credentials.getUsername());
        }

        var token = jwtTokenProvider.createAccessToken(credentials.getUsername(), user.getRoles());

        return ResponseEntity.ok(token);
    }
}
