package com.spring.springbootmfalearning.ldap;

import com.spring.springbootmfalearning.model.auth.AuthRequestDto;
import com.spring.springbootmfalearning.model.http.ApiResponse;

import java.util.Map;

public interface LdapAuthService {


    boolean isLdapConnected(String username, String password);

    ApiResponse<Map<String, Object>> login(AuthRequestDto authRequestDto);
}
