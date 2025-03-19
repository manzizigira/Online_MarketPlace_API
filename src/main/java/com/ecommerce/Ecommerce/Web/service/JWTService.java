package com.ecommerce.Ecommerce.Web.service;

import com.ecommerce.Ecommerce.Web.entity.Users;
import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    String generateToken(Users user, String secretKey);

}
