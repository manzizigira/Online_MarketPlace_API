package com.ecommerce.Ecommerce.Web.implementation;

import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsImplementation implements UserDetailsService {

    private UsersRepository repository;

    public UserDetailsImplementation(UsersRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = repository.findByUsername(username);

        System.out.println("Looking up user: " + username);

        if (user == null){
            System.out.println("User not found");
            throw new UsernameNotFoundException("User not found");
        }

        return new UserPrincipal(user);
    }
}
