package com.spring.springbootmfalearning.model.user;

import com.spring.springbootmfalearning.domain.LoginType;

import java.util.Set;
import java.util.UUID;

public record UserResponseDto(UUID id, String email, String username, Set<LoginType> loginTypeSet) {
}
