package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.dto.PersonDTO;
import com.gustavofelix.rest_spring_boot.dto.security.AccountCredentialsDTO;
import com.gustavofelix.rest_spring_boot.dto.security.TokenDTO;
import com.gustavofelix.rest_spring_boot.exception.ResourceBadRequestException;
import com.gustavofelix.rest_spring_boot.model.Person;
import com.gustavofelix.rest_spring_boot.model.User;
import com.gustavofelix.rest_spring_boot.repository.UserRepository;
import com.gustavofelix.rest_spring_boot.security.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseObject;

@Service
public class AuthService {

    Logger logger = LoggerFactory.getLogger(AuthService.class);

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

    public ResponseEntity<TokenDTO> refreshToken(String username, String refreshtoken) {
        var user = userRepository.findByUsername(username);
        TokenDTO token;

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else {
            token = jwtTokenProvider.refreshToken(refreshtoken);
        }

        return ResponseEntity.ok(token);
    }

    public AccountCredentialsDTO insert(AccountCredentialsDTO user) {
        if (user == null) throw new ResourceBadRequestException();

        logger.info("Creating a new User!");
        var entity = new User();
        entity.setFullname(user.getFullname());
        entity.setUserName(user.getUsername());
        entity.setPassword(generateHashedPassword(user.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);

        var dto = userRepository.save(entity);
        return new AccountCredentialsDTO(dto.getUsername(), dto.getPassword(), dto.getFullname());
    }

    private String generateHashedPassword(String password) {

        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder(
                "", 8, 185000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", pbkdf2Encoder);
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);

        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
        return passwordEncoder.encode(password);
    }
}