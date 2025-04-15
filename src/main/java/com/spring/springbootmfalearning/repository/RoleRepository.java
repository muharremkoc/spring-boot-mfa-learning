package com.spring.springbootmfalearning.repository;


import com.spring.springbootmfalearning.domain.Role;
import com.spring.springbootmfalearning.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Role findByRoleEnum(RoleEnum roleEnum);

    boolean existsByRoleEnum(RoleEnum roleEnum);

}