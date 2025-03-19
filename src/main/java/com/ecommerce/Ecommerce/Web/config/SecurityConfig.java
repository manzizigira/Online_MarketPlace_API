package com.ecommerce.Ecommerce.Web.config;

import com.ecommerce.Ecommerce.Web.JWT.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private UserDetailsService userDetailsService;

    private JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        System.out.println("Security filter chain initialized...");
        return httpSecurity

                .csrf(customizer -> customizer.disable()) // Disable CSRF for API authentication

                .authorizeHttpRequests(requests -> requests

                        .requestMatchers("/register", "/login", "/verify-email", "/viewLoginPage",
                                "/viewRegistrationPage", "/user/**", "/static/**","/css/**", "/js/**", "/images/**",
                                "/assignRolePage","/assignRoles","/logout")
                        .permitAll()

                        // Pages for ADMIN role (Only accessible by ADMIN users)
                        .requestMatchers("/registerCategory", "/viewCategoryPage","/viewProductsPage",
                                "/registerProducts","/products/toggleFeatured","/product/{productId}"
                                ,"/viewAdminPage", "/viewOrderManagement","/update-order","/update-orders",
                                "/delete-order","/update-category","/update-categories","/delete-category",
                                "/update-products","/update-product","/delete-product","/viewUserManagement",
                                "/update-user","/update-users","/update-users","/delete-user")
                        .hasRole("ADMIN")

                        // Pages for SHOPPER role (Only accessible by SHOPPER users)
                        .requestMatchers("/user/orders","/user/order-history","/order-details",
                                "/featured","/shop","/placeOrder","/products","/category/{categoryName}",
                                "/tag/{tag}","/search","/submit","/update/{reviewId}","/prepareOrder","/productDetails",
                                "/manage-profile", "/updateUser")
                        .hasRole("SHOPPER")

                        .anyRequest().authenticated()

                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/unauthorized")  // Redirect to unauthorized page if access is denied
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))  // Respond with 401 if unauthenticated
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No session needed for JWT
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }




    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
