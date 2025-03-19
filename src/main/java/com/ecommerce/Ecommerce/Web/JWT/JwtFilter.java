package com.ecommerce.Ecommerce.Web.JWT;

import com.ecommerce.Ecommerce.Web.implementation.SecurityService;
import com.ecommerce.Ecommerce.Web.implementation.UserDetailsImplementation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private SecurityService securityService;

    private UserDetailsImplementation userDetailsImplementation;

    @Autowired
    public JwtFilter(SecurityService securityService, UserDetailsImplementation userDetailsImplementation) {
        this.securityService = securityService;
        this.userDetailsImplementation = userDetailsImplementation;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("JwtFilter triggered");

        // Try to get JWT token from cookies
        String token = getJwtTokenFromCookies(request);
        String username = null;

        logger.info("Token from cookies: " + token);

        if (token != null) {
            username = securityService.extractUserName(token);
            logger.info("Extracted Username: " + username + " Token: " + token); // log for debugging
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsImplementation.loadUserByUsername(username);

            if (securityService.validateToken(token, userDetails)) {
                // After validation, extract roles from the token
                String roles = securityService.extractRoles(token);

                List<GrantedAuthority> authorities = new ArrayList<>();

                // Add authorities based on roles
                if (roles.contains("ADMIN")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                } else if (roles.contains("SHOPPER")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_SHOPPER"));
                }

                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Invalid token
                return;
            }
        }


        filterChain.doFilter(request, response);
    }

    private String getJwtTokenFromCookies(HttpServletRequest request) {
        // Get JWT token from the cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // Return null if no JWT token found in cookies
    }
}
