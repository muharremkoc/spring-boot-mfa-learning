package com.spring.springbootmfalearning.keycloak;

import com.spring.springbootmfalearning.domain.User;
import com.spring.springbootmfalearning.enums.LoginTypeEnum;
import com.spring.springbootmfalearning.enums.RoleEnum;
import com.spring.springbootmfalearning.exceptions.AuthenticationException;
import com.spring.springbootmfalearning.jwt.JwtService;
import com.spring.springbootmfalearning.model.auth.AuthRequestDto;
import com.spring.springbootmfalearning.model.auth.AuthResponseDto;
import com.spring.springbootmfalearning.model.http.ApiResponse;
import com.spring.springbootmfalearning.repository.LoginTypeRepository;
import com.spring.springbootmfalearning.repository.RoleRepository;
import com.spring.springbootmfalearning.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeycloakAuthServiceImpl implements KeycloakAuthService {

    final Logger log= LoggerFactory.getLogger(KeycloakAuthServiceImpl.class);

    private final TokenServiceImpl tokenService;

    //private final KeyCloakJwtService keyCloakJwtService;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final LoginTypeRepository loginTypeRepository;

    private final JwtService jwtService;


    @Override
    public ApiResponse<Map<String, Object>> login(AuthRequestDto authRequestDto) throws AuthenticationException {
        log.info("Keycloak Login Service started for user: {}", authRequestDto.username());
        String keyCloakToken = tokenService.keyCloakClientCredentialsConnect(authRequestDto);
        if (keyCloakToken != null && !keyCloakToken.isEmpty()) {
            KeycloakUserInfo keycloakUserInfo=tokenService.getUserInfo(keyCloakToken);
            Optional<User> keyCloakAuthenticatedUser = userRepository.findByUsername(authRequestDto.username());

            // E-posta kontrolü ve oturum tipini güncelleme
            if (keyCloakAuthenticatedUser.isPresent()) {
                User user = keyCloakAuthenticatedUser.get();
                String emailFromToken = keycloakUserInfo.email();

                if (!user.getEmail().equals(emailFromToken)) {
                    // E-posta eşleşmiyorsa mevcut oturum tipine ekle
                    user.getLoginTypes().add(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.KEYCLOAK));
                    userRepository.save(user);
                } else {
                    // E-posta eşleşiyorsa mevcut oturum tipine ekle
                    user.getLoginTypes().add(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.KEYCLOAK));
                    userRepository.save(user);
                }
            } else {
                // Kullanıcı adı mevcut değilse yeni kullanıcı oluştur
                keyCloakAuthenticatedUser = Optional.of(createAndSaveUser(keyCloakToken));
            }
            authenticateUser(keyCloakAuthenticatedUser.get());
            String accessToken = jwtService.generateToken(
                    keyCloakAuthenticatedUser.get().getId().toString(),
                    keyCloakAuthenticatedUser.get().getUsername(),
                    keyCloakAuthenticatedUser.get().getEmail(),
                    keyCloakAuthenticatedUser.get().getRoles());
            AuthResponseDto authResponseDto=new AuthResponseDto(keyCloakAuthenticatedUser.get().getEmail(), accessToken,LoginTypeEnum.KEYCLOAK);
            return ApiResponse.default_CREATED(Map.of("accessUser", authResponseDto));
        } else
            throw new AuthenticationException("Invalid token");
    }

    private User createAndSaveUser(String keyCloakToken) {
        try {

            KeycloakUserInfo keycloakUserInfo=tokenService.getUserInfo(keyCloakToken);

            String username = keycloakUserInfo.preferred_username();
            String email = keycloakUserInfo.email();
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setLoginTypes(Set.of(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.KEYCLOAK)));
            newUser.setEmail(email);
            newUser.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.ROLE_USER)));
            userRepository.save(newUser);
            log.info("User created and saved: {}", newUser.getUsername());
            return newUser;
        } catch (Exception e) {
            log.error("Failed to create and save user", e);
            throw new IllegalStateException("User creation failed", e);
        }
    }

    private void authenticateUser(User user) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", user.getUsername(), e);
            throw new IllegalStateException("Authentication failed", e);
        }
    }
}