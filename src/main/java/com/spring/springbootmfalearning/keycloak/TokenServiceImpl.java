package com.spring.springbootmfalearning.keycloak;


import com.spring.springbootmfalearning.exceptions.AuthenticationException;
import com.spring.springbootmfalearning.model.auth.AuthRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class TokenServiceImpl {


    private static final String GRANT_TYPE = "password";
    private static final String ACCESS_TOKEN_KEY = "access_token";

    @Value("${keycloak.auth-token-url}")
    private String keycloakTokenUrl;


    @Value("${keycloak.auth-user-info-url}")

    private String keycloakUserInfoUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public String keyCloakClientCredentialsConnect(AuthRequestDto authRequest) throws AuthenticationException {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", authRequest.username());
        body.add("password", authRequest.password());
        body.add("grant_type", GRANT_TYPE);
        body.add("scope", "openid");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.exchange(keycloakTokenUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                response.getBody();
                return Objects.requireNonNull(response.getBody().get(ACCESS_TOKEN_KEY)).toString();
            }

            throw new AuthenticationException("Authentication failed with status: " + response.getStatusCode());
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }


    public KeycloakUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<KeycloakUserInfo> response = restTemplate.exchange(keycloakUserInfoUrl,
                HttpMethod.GET,
                entity,
                KeycloakUserInfo.class
        );

        return response.getBody();
    }


}