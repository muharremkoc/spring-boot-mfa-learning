package com.spring.springbootmfalearning.service.auth;

import com.spring.springbootmfalearning.exceptions.BadCredentialsException;
import com.spring.springbootmfalearning.exceptions.ConflictException;
import com.spring.springbootmfalearning.model.auth.AuthRequestDto;
import com.spring.springbootmfalearning.model.http.ApiResponse;
import com.spring.springbootmfalearning.model.user.RegisterUserRequestDto;

import java.util.Map;

public interface AuthService {

    ApiResponse<Map<String, String>> register(RegisterUserRequestDto registerUserRequestDto) throws ConflictException;

    ApiResponse<Map<String, Object>> login(AuthRequestDto authRequestDto) throws  BadCredentialsException;


}

