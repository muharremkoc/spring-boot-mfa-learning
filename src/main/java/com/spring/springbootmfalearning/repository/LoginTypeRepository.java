package com.spring.springbootmfalearning.repository;

import com.spring.springbootmfalearning.domain.LoginType;
import com.spring.springbootmfalearning.enums.LoginTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginTypeRepository extends JpaRepository<LoginType, UUID> {

    LoginType findByLoginTypeEnum(LoginTypeEnum loginTypeEnum);

    boolean existsByLoginTypeEnum(LoginTypeEnum loginTypeEnum);

}