package com.ecommerce.Ecommerce.Web.repository;

import com.ecommerce.Ecommerce.Web.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:4200")
public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByEmailVerificationToken(String theEmail);

    Users findByUsername(String theUsername);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Users findByEmail (String email);
}
