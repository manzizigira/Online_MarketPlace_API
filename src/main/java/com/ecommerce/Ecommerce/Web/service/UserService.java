package com.ecommerce.Ecommerce.Web.service;

import com.ecommerce.Ecommerce.Web.entity.Users;

import java.util.List;

public interface UserService {

    Users registerUser(Users users);
    void updateUser(Users users);
    Users getUserByEmailVerificationToken(String email);
    boolean updateUserFields(Users user);
    String verifyUser(Users users);
    Users getUsername(String username);
    Users getUserById (Long userId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<Users> fetchUsers();
    void deleteUserId(Long id);
    Users getUserEmail(String email);
    void sendVerificationEmail(Users saveUser);
}
