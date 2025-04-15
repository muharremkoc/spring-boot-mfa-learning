package com.spring.springbootmfalearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.spring.springbootmfalearning.enums.LoginTypeEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Table(name = "login_types")
@Getter
@Setter
public class LoginType {


    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR) // UUID'yi VARCHAR olarak saklar
    @Column(name = "login_type_id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginTypeEnum loginTypeEnum;

    @ManyToMany(mappedBy = "loginTypes", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> users;

    public LoginType() {
    }

    public LoginType(LoginTypeEnum loginTypeEnum) {
        this.loginTypeEnum = loginTypeEnum;
    }

    public LoginType(UUID id, LoginTypeEnum loginTypeEnum, Set<User> users) {
        this.id = id;
        this.loginTypeEnum = loginTypeEnum;
        this.users = users;
    }
}