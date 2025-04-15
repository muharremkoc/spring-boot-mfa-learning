package com.spring.springbootmfalearning.jwt;

import com.spring.springbootmfalearning.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final Environment env;

    public JwtService(Environment env) {
        this.env = env;
    }

    private String getSecretKey() {
        String secretKey = env.getProperty("aes.secret.key");
        if (secretKey.trim().isEmpty()) {
            throw new IllegalStateException("Secret key is missing in environment properties!");
        }
        return secretKey;
    }

    private SecretKey getSigningKey() {
        try {
            return Keys.hmacShaKeyFor(getSecretKey().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error while getting signing key", e);
            throw new IllegalStateException("Signing key generation failed", e);
        }
    }

    // Generate token with given user name
    public String generateToken(String userId, String userName, String userEmail, Set<Role> userRoles) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId,userName,userEmail,userRoles);
    }

    // Create a JWT token with specified claims and subject (username)
    private String createToken(Map<String, Object> claims, String userId,String userName,String userEmail,Set<Role> userRoles) {
        return Jwts.builder()
                .setClaims(claims)
                .claim("roles", new ArrayList<>(userRoles))
                .setSubject(String.format("%s,%s,%s", userId, userName, userEmail))
                .setIssuer("KocProduction")
                .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Token valid for 30 minutes
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    // Extract the username from the token
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject).split(",")[1];
        } catch (Exception e) {
            logger.error("Error extracting username from token", e);
            return null;
        }
    }

    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}