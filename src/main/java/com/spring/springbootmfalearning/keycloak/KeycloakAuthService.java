package com.spring.springbootmfalearning.keycloak;


import com.spring.springbootmfalearning.exceptions.AuthenticationException;
import com.spring.springbootmfalearning.model.auth.AuthRequestDto;
import com.spring.springbootmfalearning.model.http.ApiResponse;

import java.util.Map;

public interface KeycloakAuthService {

    ApiResponse<Map<String, Object>> login(AuthRequestDto authRequestDto) throws AuthenticationException;
}