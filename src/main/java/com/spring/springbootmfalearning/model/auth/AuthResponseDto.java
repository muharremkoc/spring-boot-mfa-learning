package com.spring.springbootmfalearning.model.auth;

import com.spring.springbootmfalearning.enums.LoginTypeEnum;

public record AuthResponseDto(String email, String token, LoginTypeEnum loginTypeEnum) {
}
