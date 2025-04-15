package com.spring.springbootmfalearning.controller;

import com.spring.springbootmfalearning.model.http.ApiResponse;
import com.spring.springbootmfalearning.model.user.UpdateUserRequestDto;
import com.spring.springbootmfalearning.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
public class UserController {

     UserService userService;



    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUsers() {
        Map<String,Object> result = userService.getUsers().getData();
        var apiResponse = ApiResponse.default_OK(result);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUser(@PathVariable("id") UUID id) {
        Map<String,Object> result = userService.getUserById(id).getData();
        ApiResponse<Map<String, Object>> apiResponse =  ApiResponse.default_OK(result);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUser(@PathVariable("id") UUID id, @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        Map<String,Object> result = userService.updateUser(id,updateUserRequestDto).getData();
        ApiResponse<Map<String, Object>> apiResponse =  ApiResponse.default_OK(result);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteUser(@PathVariable("id") UUID id) {
        Map<String,String> result = userService.deleteUser(id).getData();
        ApiResponse<Map<String, String>> apiResponse =  ApiResponse.default_ACCEPTED(result);


        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(apiResponse);
    }
}
