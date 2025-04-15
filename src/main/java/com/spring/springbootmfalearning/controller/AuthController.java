package com.spring.springbootmfalearning.controller;


import com.spring.springbootmfalearning.exceptions.AuthenticationException;
import com.spring.springbootmfalearning.exceptions.BadCredentialsException;
import com.spring.springbootmfalearning.exceptions.ConflictException;
import com.spring.springbootmfalearning.keycloak.KeycloakAuthService;
import com.spring.springbootmfalearning.ldap.LdapAuthService;
import com.spring.springbootmfalearning.model.auth.AuthRequestDto;
import com.spring.springbootmfalearning.model.http.ApiResponse;
import com.spring.springbootmfalearning.model.user.RegisterUserRequestDto;
import com.spring.springbootmfalearning.service.auth.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthController {

    AuthService authService;

    LdapAuthService ldapAuthService;

    KeycloakAuthService keycloakAuthService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, String>>> register(@Valid @RequestBody RegisterUserRequestDto registerRequest) throws  ConflictException  {
        var result = authService.register(registerRequest).getData();
        var apiResponse = ApiResponse.default_CREATED(result);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody AuthRequestDto authRequestDto) throws BadCredentialsException {
        var result = authService.login(authRequestDto).getData();
        var apiResponse = ApiResponse.default_CREATED(result);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @PostMapping("/ldap")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ldap(@Valid @RequestBody AuthRequestDto authRequestDto) throws BadCredentialsException {
        var result = ldapAuthService.login(authRequestDto).getData();
        var apiResponse = ApiResponse.default_CREATED(result);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @PostMapping("/keycloak")
    public ResponseEntity<ApiResponse<Map<String, Object>>> keycloak(@Valid @RequestBody AuthRequestDto authRequestDto) throws BadCredentialsException, AuthenticationException {
        var result = keycloakAuthService.login(authRequestDto).getData();
        var apiResponse = ApiResponse.default_CREATED(result);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

}
