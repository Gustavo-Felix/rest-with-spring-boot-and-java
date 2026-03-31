package com.gustavofelix.rest_spring_boot.controllers;

import com.gustavofelix.rest_spring_boot.dto.security.AccountCredentialsDTO;
import com.gustavofelix.rest_spring_boot.dto.security.TokenDTO;
import com.gustavofelix.rest_spring_boot.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Auth", description = "Endpoints for authentication and authorization!")
@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Sign in with username and password!", description = "Returns a token to be used in other requests!")
    @PostMapping(value = "/signin")
    public ResponseEntity<?> signIn(@RequestBody AccountCredentialsDTO credentials) {
        if (checkCredentials(credentials)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

        var token = authService.signIn(credentials);

        if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client request!");


        return ResponseEntity.ok().body(token);
    }

    @Operation(summary = "Refresh token with username and refresh token!", description = "Returns a token to be used in other requests!")
    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity<?> refresh(@PathVariable("username") String username, @RequestHeader("Authorization") String refreshtoken) {
        if (parametersAreInvalid(username, refreshtoken)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

        var token = authService.refreshToken(username, refreshtoken);

        if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client request!");

        return ResponseEntity.ok().body(token);
    }

    private boolean parametersAreInvalid(String username, String refreshtoken) {
        return StringUtils.isBlank(username) || StringUtils.isBlank(refreshtoken);
    }

    private boolean checkCredentials(AccountCredentialsDTO credentials) {
        return credentials == null ||
                StringUtils.isBlank(credentials.getUsername()) ||
                StringUtils.isBlank(credentials.getPassword());
    }
}
