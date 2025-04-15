package com.spring.springbootmfalearning.ldap;


import com.spring.springbootmfalearning.domain.User;
import com.spring.springbootmfalearning.enums.LoginTypeEnum;
import com.spring.springbootmfalearning.enums.RoleEnum;
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
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LdapAuthServiceImpl implements LdapAuthService{

    final Logger log= LoggerFactory.getLogger(LdapAuthServiceImpl.class);

    final Environment environment;

    final UserRepository userRepository;

    final RoleRepository roleRepository;

    final LoginTypeRepository loginTypeRepository;

    final Environment env;


    final JwtService jwtService;



    @Override
    public boolean isLdapConnected(String username, String password) {
        Hashtable<String, String> env = createLdapEnvironment(username, password);
        DirContext dirContext = null;
        try {
            dirContext = new InitialDirContext(env);
            return true;
        } catch (Exception e) {
            log.error("LDAP connection failed for user: {}", username, e);
            return false;
        } finally {
            if (dirContext != null) {
                try {
                    dirContext.close();
                } catch (Exception e) {
                    log.error("Failed to close DirContext", e);
                }
            }
        }
    }

    private Hashtable<String, String> createLdapEnvironment(String username, String password) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(DirContext.INITIAL_CONTEXT_FACTORY, environment.getProperty("ldap.initial.context.factory"));
        env.put(DirContext.PROVIDER_URL, environment.getProperty("ldap.urls"));
        env.put(DirContext.SECURITY_AUTHENTICATION, environment.getProperty("ldap.security.authentication"));
        env.put(DirContext.SECURITY_PRINCIPAL, "cn=" + username + ",cn=users," + environment.getProperty("ldap.base.dn"));
        env.put(DirContext.SECURITY_CREDENTIALS, password);
        return env;
    }

    @Override
    public ApiResponse<Map<String, Object>> login(AuthRequestDto authRequestDto) {
        log.info("Ldap Login Service started for user: {}", authRequestDto.username());
        if (isLdapConnected(authRequestDto.username(), authRequestDto.password())) {
            Optional<User> ldapAuthenticatedUser = userRepository.findByUsername(authRequestDto.username());

            // E-posta kontrolü ve oturum tipini güncelleme
            if (ldapAuthenticatedUser.isPresent()) {
                User user = ldapAuthenticatedUser.get();
                if (!user.getEmail().equals(authRequestDto.username().concat(env.getProperty("ldap.domain")))) {
                    // E-posta eşleşmiyorsa mevcut oturum tipine ekle
                    user.getLoginTypes().add(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.LDAP));
                    userRepository.save(user);
                } else {
                    // E-posta eşleşiyorsa mevcut oturum tipine ekle
                    user.getLoginTypes().add(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.LDAP));
                    userRepository.save(user);
                }
            } else {
                // Kullanıcı adı mevcut değilse yeni kullanıcı oluştur
                ldapAuthenticatedUser = Optional.of(createAndSaveUser(authRequestDto));
            }
            authenticateUser(ldapAuthenticatedUser.get());
            String accessToken = jwtService.generateToken(
                    ldapAuthenticatedUser.get().getId().toString(),
                    ldapAuthenticatedUser.get().getUsername(),
                    ldapAuthenticatedUser.get().getEmail(),
                    ldapAuthenticatedUser.get().getRoles()
            );
            AuthResponseDto authResponseDto = new AuthResponseDto(ldapAuthenticatedUser.get().getEmail(),accessToken,LoginTypeEnum.LDAP);
            return ApiResponse.default_CREATED(Map.of("accessUser", authResponseDto));
        } else {
            throw new BadCredentialsException("Ldap Kullanıcı bilgileri yanlış");
        }
    }

    private User createAndSaveUser(AuthRequestDto authRequestDto) {
        try {
            User user = new User();
            user.setUsername(authRequestDto.username());
            user.setEmail(authRequestDto.username().concat(env.getProperty("ldap.domain")));
            user.setLoginTypes(Set.of(loginTypeRepository.findByLoginTypeEnum(LoginTypeEnum.LDAP)));
            user.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.ROLE_USER)));
            userRepository.save(user);
            log.info("User created and saved: {}", user.getUsername());
            return user;
        } catch (Exception e) {
            log.error("Failed to create and save user: {}", authRequestDto.username(), e);
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