package com.spring.springbootmfalearning.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    UUID id;

    String email;

    String username;

    String password;


    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    // CascadeType.PERSIST, // Gerekirse açabilirsin
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    // CascadeType.PERSIST, // Gerekirse açabilirsin
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "user_logins",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "login_type_id")
    )
    private Set<LoginType> loginTypes = new HashSet<>();


}
