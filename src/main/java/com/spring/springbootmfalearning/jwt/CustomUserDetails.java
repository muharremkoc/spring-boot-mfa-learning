package com.spring.springbootmfalearning.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.spring.springbootmfalearning.domain.Role;
import com.spring.springbootmfalearning.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Set<Role> roleSet;


    public CustomUserDetails(User user) {
        this.username = user.getUsername();
        this.roleSet = user.getRoles();
        this.password = user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        List<Role> roles = new ArrayList<>(roleSet);
        for (Object object : roles) {
            Role role = mapper.convertValue(object, Role.class);
            authorities.add(new SimpleGrantedAuthority(role.getRoleEnum().name()));
        }
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}