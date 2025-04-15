package com.spring.springbootmfalearning.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.springbootmfalearning.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;
import java.util.UUID;


@Builder
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR) // UUID'yi VARCHAR olarak saklar
    @Column(name = "role_id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum roleEnum;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> users;

    public Role() {
    }

    public Role(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }

    public Role(UUID id, RoleEnum roleEnum, Set<User> users) {
        this.id = id;
        this.roleEnum = roleEnum;
        this.users = users;
    }
}
