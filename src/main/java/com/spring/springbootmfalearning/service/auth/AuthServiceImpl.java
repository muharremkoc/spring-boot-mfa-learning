package com.spring.springbootmfalearning.service.auth;

import com.spring.springbootmfalearning.domain.User;
import com.spring.springbootmfalearning.enums.LoginTypeEnum;
import com.spring.springbootmfalearning.enums.RoleEnum;
import com.spring.springbootmfalearning.exceptions.BadCredentialsException;
import com.spring.springbootmfalearning.exceptions.ConflictException;
import com.spring.springbootmfalearning.jwt.JwtService;
import com.spring.springbootmfalearning.model.auth.AuthRequestDto;
import com.spring.springbootmfalearning.model.auth.AuthResponseDto;
import com.spring.springbootmfalearning.model.http.ApiResponse;
import com.spring.springbootmfalearning.model.user.RegisterUserRequestDto;
import com.spring.springbootmfalearning.repository.LoginTypeRepository;
import com.spring.springbootmfalearning.repository.RoleRepository;
import com.spring.springbootmfalearning.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;

    LoginTypeRepository loginTypeRepository;

    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    JwtService jwtService;

    AuthenticationManager authenticationManager;


    @Override
    public ApiResponse<Map<String, String>> register(RegisterUserRequestDto registerUserRequestDto) throws ConflictException {
        // Kullanıcıyı bul
        Optional<User> existingUser = userRepository.findByUsername(registerUserRequestDto.username());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // E-posta kontrolü
            if (user.getEmail().equals(registerUserRequestDto.email())) {
                // Kullanıcı adı kontrolü
                if (userRepository.existsByUsername(registerUserRequestDto.username())) {
                    // Eğer kullanıcı adı mevcutsa ve e-posta farklıysa, sadece yeni login tipi ekle
                    if (!user.getLoginTypes().contains(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.NORMAL))) {
                        user.setPassword(passwordEncoder.encode(registerUserRequestDto.password()));

                        user.getLoginTypes().add(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.NORMAL));
                        userRepository.save(user);
                    }
                    // Kullanıcı zaten mevcut, sadece normal login eklenmiş oldu
                    return ApiResponse.default_CREATED(Map.of("email", user.getEmail()));
                }
            } else {
                // Kullanıcı adı mevcut değilse ve e-posta farklıysa, yeni login tipi ekle
                if (userRepository.existsByUsername(registerUserRequestDto.username())) {
                    User existingUserByUsername = userRepository.findByUsername(registerUserRequestDto.username()).orElseThrow();
                    if (!existingUserByUsername.getLoginTypes().contains(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.NORMAL))) {
                        existingUserByUsername.setPassword(passwordEncoder.encode(registerUserRequestDto.password()));
                        existingUserByUsername.getLoginTypes().add(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.NORMAL));
                        userRepository.save(existingUserByUsername);
                    }
                    return ApiResponse.default_CREATED(Map.of("email", existingUserByUsername.getEmail()));
                }
            }
        }

        // Yeni kullanıcı oluştur
        if (userRepository.existsByEmail(registerUserRequestDto.email()))
            throw new ConflictException("Bu E-posta Zaten Mevcut");
        if (userRepository.existsByUsername(registerUserRequestDto.username()))
            throw new ConflictException("Bu Kullanıcı Adı Zaten Mevcut");

        User newUser = new User();
        newUser.setUsername(registerUserRequestDto.username());
        newUser.setEmail(registerUserRequestDto.email());
        newUser.setPassword(passwordEncoder.encode(registerUserRequestDto.password()));
        newUser.setLoginTypes(Set.of(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.NORMAL)));
        newUser.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.ROLE_USER)));
        userRepository.save(newUser);

        Map<String, String> data = Map.of("email", newUser.getEmail());
        return ApiResponse.default_CREATED(data);
    }

    @Override
    public ApiResponse<Map<String, Object>> login(AuthRequestDto authRequestDto) throws BadCredentialsException {

        User authenticatedUser = userRepository.findByUsername(authRequestDto.username()).orElseThrow(() -> new BadCredentialsException(("Geçersiz Kullanıcı Adı ve Şifre")));

        boolean isPasswordMatch = passwordEncoder.matches(authRequestDto.password(), authenticatedUser.getPassword());
        if (!isPasswordMatch) {
            throw new BadCredentialsException("Şifreler eşleşmedi");
        }


        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.username(), authRequestDto.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            throw new IllegalStateException("Authentication failed " + e);
        }

        String access_token = jwtService.generateToken(authenticatedUser.getId().toString(), authenticatedUser.getUsername(), authenticatedUser.getEmail(), authenticatedUser.getRoles());

        AuthResponseDto authResponseDto = new AuthResponseDto(authenticatedUser.getEmail(),access_token,LoginTypeEnum.NORMAL);

        Map<String, Object> data = Map.of(
                "accessUser", authResponseDto
        );

        return ApiResponse.default_CREATED(data);
    }
}