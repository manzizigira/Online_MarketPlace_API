package com.ecommerce.Ecommerce.Web.JWT;

import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.service.JWTService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class JWTImplementation implements JWTService {

    @Override
    public String generateToken(Users user, String secretKey) {
        Long userId = user.getId(); // Ensure the 'Users' object has a valid 'getId' method

        return Jwts.builder()
                .setSubject(user.getUsername()) // Set username as the subject (or any other field you want)
                .claim("userId", userId) // Add the userId as a claim in the JWT token
                .claim("roles", user.getRoles()) // Add the roles to the token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000 * 10)) // Set expiration time
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))) // Signing with the secret key
                .compact();
    }





}
