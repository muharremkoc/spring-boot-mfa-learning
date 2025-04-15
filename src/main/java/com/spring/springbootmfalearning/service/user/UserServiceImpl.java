package com.spring.springbootmfalearning.service.user;

import com.spring.springbootmfalearning.domain.User;
import com.spring.springbootmfalearning.model.http.ApiResponse;
import com.spring.springbootmfalearning.model.user.UpdateUserRequestDto;
import com.spring.springbootmfalearning.model.user.UserResponseDto;
import com.spring.springbootmfalearning.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserServiceImpl implements UserService{

    UserRepository userRepository;

    @Override
    public ApiResponse<Map<String, Object>> getUsers() {
        List<User> users = userRepository.findAll();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("users", mapToUserResponseDtoList(users));
        return ApiResponse.default_OK(responseMap);
    }

    @Override
    public  ApiResponse<Map<String, Object>> getUserById(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("user", mapToUserResponseDto(optionalUser.get()));
            return ApiResponse.default_OK(responseMap);
        }
        return new ApiResponse<>();
    }

    @Override
    public  ApiResponse<Map<String, Object>> updateUser(UUID id, UpdateUserRequestDto userDto) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmail(userDto.email());
            user.setUsername(userDto.username());
            userRepository.save(user);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("user", mapToUserResponseDto(optionalUser.get()));
            return ApiResponse.default_OK(responseMap);
        }
        return new ApiResponse<>();
    }

    @Override
    public ApiResponse<Map<String, String>> deleteUser(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("user","User Deleted Successfully");
            userRepository.delete(user);
            return ApiResponse.default_ACCEPTED(responseMap);
        }
        return new ApiResponse<>();
    }

    private List<UserResponseDto> mapToUserResponseDtoList(List<User> users) {
        List<UserResponseDto> userResponseDtoList= new ArrayList<>();
        for (User user : users) {
            userResponseDtoList.add(new UserResponseDto(user.getId(),user.getEmail(),user.getPassword(),user.getLoginTypes()));
        }
        return userResponseDtoList;
    }

    private UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto(user.getId(),user.getEmail(),user.getPassword(),user.getLoginTypes());
    }
}
