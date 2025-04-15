package com.spring.springbootmfalearning.loader;

import com.spring.springbootmfalearning.domain.LoginType;
import com.spring.springbootmfalearning.domain.Role;
import com.spring.springbootmfalearning.enums.LoginTypeEnum;
import com.spring.springbootmfalearning.enums.RoleEnum;
import com.spring.springbootmfalearning.repository.LoginTypeRepository;
import com.spring.springbootmfalearning.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class DbLoader implements ApplicationRunner {

   private final RoleRepository roleRepository;

   private final LoginTypeRepository loginTypeRepository;



    @Override
    public void run(ApplicationArguments args) {

        Arrays.stream(RoleEnum.values())
                .filter(roleEnum -> !roleRepository.existsByRoleEnum(roleEnum))
                .map(roleEnum -> Role.builder().roleEnum(roleEnum).build())
                .forEach(roleRepository::save);

        Arrays.stream(LoginTypeEnum.values())
                .filter(loginTypeEnum -> !loginTypeRepository.existsByLoginTypeEnum(loginTypeEnum))
                .map(loginTypeEnum -> LoginType.builder().loginTypeEnum(loginTypeEnum).build())
                .forEach(loginTypeRepository::save);


    }


}