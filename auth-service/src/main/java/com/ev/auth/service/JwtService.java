package com.ev.auth.service;

import com.ev.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${jwt.refresh-token.expiration}")
    private long refreshExpiration;
    
    @Value("${jwt.issuer:ev-platform}")
    private String issuer;
    
    @Value("${jwt.audience:ev-saas}")
    private String audience;
    
    /**
     * Generate a JWT token for a user
     * @param user The user details
     * @return JWT token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .toList());
        claims.put("email", user.getEmail());
        claims.put("name", String.format("%s %s", user.getFirstName(), user.getLastName()));
        
        return generateToken(claims, user);
    }
    
    /**
     * Generate a refresh token for a user
     * @param user The user details
     * @return Refresh token
     */
    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }
    
    /**
     * Validate a token for a user
     * @param token The token to validate
     * @param userDetails The user details
     * @return True if valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    /**
     * Extract username from token
     * @param token The token
     * @return Username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract user ID from token
     * @param token The token
     * @return User ID
     */
    public UUID extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return UUID.fromString(subject);
    }
    
    /**
     * Extract expiration from token
     * @param token The token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Check if token is expired
     * @param token The token
     * @return True if expired
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Extract a claim from token
     * @param token The token
     * @param claimsResolver The claims resolver function
     * @return The claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Generate a token with claims for a user
     * @param extraClaims The extra claims
     * @param user The user
     * @return JWT token
     */
    private String generateToken(Map<String, Object> extraClaims, User user) {
        return buildToken(extraClaims, user, jwtExpiration);
    }
    
    /**
     * Build a token with claims for a user
     * @param extraClaims The extra claims
     * @param user The user
     * @param expiration The expiration time
     * @return JWT token
     */
    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setIssuer(issuer)
                .setAudience(audience)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extract all claims from token
     * @param token The token
     * @return All claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Get signing key for JWT
     * @return The signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 