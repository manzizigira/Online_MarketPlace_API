package com.ecommerce.Ecommerce.Web.implementation;

import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.repository.UsersRepository;
import com.ecommerce.Ecommerce.Web.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class SecurityService {

    private UsersRepository usersRepository;

    @Autowired
    public SecurityService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public String extractUserName(String token) {
        return getSubjectFromTokenWithoutVerification(token);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        // Extract the userId claim instead of the subject
        Object userIdObj = claims.get("userId");
        if (userIdObj != null) {
            return Long.parseLong(userIdObj.toString()); // Convert to Long
        }
        throw new IllegalArgumentException("User ID is missing from the token");
    }

    public String extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", String.class);
    }

    public Claims extractAllClaims(String token) {
        String subject = getSubjectFromTokenWithoutVerification(token);

        Users user = usersRepository.findByUsername(subject);
        if (user == null){
            throw new UsernameNotFoundException("User not found for token validation");
        }
        String secretKey = user.getJwtSecretKey();

        try {
            return Jwts.parser()
                    .setSigningKey(getKey(secretKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            throw new RuntimeException("Failed to verify token", e);
        }
    }

    private Key getKey(String secretKey){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getSubjectFromTokenWithoutVerification(String token) {
        try {
            // JWT structure: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid token format");
            }
            // Decode the payload (Base64 URL encoded)
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            // A very simple parsing for "sub" claim (in production, use a robust JSON parser)
            int subIndex = payloadJson.indexOf("\"sub\":\"");
            if (subIndex == -1) {
                throw new IllegalArgumentException("Subject not found in token");
            }
            int start = subIndex + 7;
            int end = payloadJson.indexOf("\"", start);
            return payloadJson.substring(start, end);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract subject from token", e);
        }
    }

}

