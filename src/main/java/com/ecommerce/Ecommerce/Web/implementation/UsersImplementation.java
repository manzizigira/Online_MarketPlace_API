package com.ecommerce.Ecommerce.Web.implementation;

import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.repository.UsersRepository;
import com.ecommerce.Ecommerce.Web.service.JWTService;
import com.ecommerce.Ecommerce.Web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersImplementation implements UserService {


    private AuthenticationManager authenticationManager;
    private SecurityService securityService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    private JavaMailSender javaMailSender;

    private JWTService jwtService;

    private UsersRepository usersRepository;
    @Autowired
    public UsersImplementation(UsersRepository usersRepository, AuthenticationManager manager, @Lazy SecurityService securityService, JavaMailSender javaMailSender, JWTService jwtService) {
        this.authenticationManager = manager;
        this.securityService = securityService;
        this.javaMailSender = javaMailSender;
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
    }

    @Override
    public Users registerUser(Users users) {

        users.setPassword(bCryptPasswordEncoder.encode(users.getPassword())); // Encrypt password

        // Generate a JWT secret key and email verification token
        String secretKey = generateSecretKeyForUser();
        users.setJwtSecretKey(secretKey);

        String token = UUID.randomUUID().toString();
        users.setEmailVerificationToken(token);

        Users savedUser = usersRepository.save(users);

        sendVerificationEmail(savedUser); // Send email verification
        return savedUser;
    }

    @Override
    public void updateUser(Users users) {
        // Ensure the user exists in the database before updating
        if (users != null && users.getId() != null) {
            usersRepository.save(users);  // This will update the user if they already exist, or insert if not
        } else {
            throw new IllegalArgumentException("User cannot be null or without an ID");
        }
    }

    private String generateSecretKeyForUser() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }

    public void sendVerificationEmail(Users saveUser) {
        String subject = "Verify your email Address \n";
        String url = " http://localhost:8080/verify-email?token="+saveUser.getEmailVerificationToken();
        String body = "Please click the link to verify your email" + url;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(saveUser.getEmail());
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }


    @Override
    public Users getUserByEmailVerificationToken(String email) {
        return usersRepository.findByEmailVerificationToken(email);
    }

    @Override
    public boolean updateUserFields(Users user) {
        Optional<Users> existingUserOpt = usersRepository.findById(user.getId());

        if (existingUserOpt.isPresent()) {
            Users existingUser = existingUserOpt.get();

            // Update only specific fields
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                existingUser.setEmail(user.getEmail());
            }
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                existingUser.setUsername(user.getUsername());
            }
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                existingUser.setFirstName(user.getFirstName());
            }
            if (user.getDob() != null) {
                existingUser.setDob(user.getDob());
            }

            if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                existingUser.setPhoneNumber(user.getPhoneNumber());
            }

            // Save the updated user back to the database
            usersRepository.save(existingUser);
            return true;
        }

        return false;
    }

    @Override
    public String verifyUser(Users users) {
        System.out.println("Attempting to authenticate user: " + users.getUsername());
        // Fetch the user from the database again to ensure it's up to date
        Users fetchedUser = usersRepository.findByUsername(users.getUsername());
        System.out.println("User fetched from DB: " + fetchedUser.getUsername() + ", JWT Secret Key: " + fetchedUser.getJwtSecretKey());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(users.getUsername(), users.getPassword())
        );

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(fetchedUser, fetchedUser.getJwtSecretKey());
        }

        return "Fail";
    }


    @Override
    public Users getUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public Users getUserById(Long userId) {
        Optional<Users> getUserId = usersRepository.findById(userId);
        Users users = null;
        if (getUserId.isPresent()){
            users = getUserId.get();
        }else {
            throw new RuntimeException("User not found!");
        }
        return users;
    }

    @Override
    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return usersRepository.existsByUsername(username);
    }

    @Override
    public List<Users> fetchUsers() {
        return usersRepository.findAll();
    }

    @Override
    public void deleteUserId(Long id) {
        usersRepository.deleteById(id);
    }

    @Override
    public Users getUserEmail(String email) {
        return usersRepository.findByEmail(email);
    }
}
