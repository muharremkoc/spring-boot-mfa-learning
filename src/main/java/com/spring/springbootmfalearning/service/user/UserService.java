package com.spring.springbootmfalearning.service.user;

import com.spring.springbootmfalearning.model.http.ApiResponse;
import com.spring.springbootmfalearning.model.user.UpdateUserRequestDto;

import java.util.Map;
import java.util.UUID;

public interface UserService {

    ApiResponse<Map<String, Object>> getUsers();

    ApiResponse<Map<String, Object>> getUserById(UUID id);

    ApiResponse<Map<String, Object>> updateUser(UUID id, UpdateUserRequestDto userDto);

    ApiResponse<Map<String, String>> deleteUser(UUID id);
}
