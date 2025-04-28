package com.ntt.userapi.service.impl;

import com.ntt.userapi.exception.EmailAlreadyExistsException;
import com.ntt.userapi.exception.InvalidEmailFormatException;
import com.ntt.userapi.exception.InvalidPasswordFormatException;
import com.ntt.userapi.model.dto.CreateUserRequest;
import com.ntt.userapi.model.dto.CreateUserResponse;
import com.ntt.userapi.model.entity.Phone;
import com.ntt.userapi.model.entity.User;
import com.ntt.userapi.repository.UserRepository;
import com.ntt.userapi.service.UserService;
import com.ntt.userapi.util.JwtTokenUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service // Mark as a Spring service component
@Transactional // All methods in this service will run in a transaction
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    // Inject validation regexes from properties
    @Value("${app.validation.email-regex}")
    private String emailRegex;

    @Value("${app.validation.password-regex}")
    private String passwordRegex;


    // Constructor injection of dependencies
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        // 1. Validate Email Format
        if (!request.getEmail().matches(emailRegex)) {
            throw new InvalidEmailFormatException();
        }

        // 2. Check if Email Already Exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // 3. Validate Password Format
        if (!request.getPassword().matches(passwordRegex)) {
            throw new InvalidPasswordFormatException();
        }

        // 4. Map DTO to Entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Encode password before setting
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set timestamps and active status
        LocalDateTime now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now); // For new users, lastLogin is same as creation
        user.setActive(true);

        // Map and associate phones
        if (request.getPhones() != null) {
            user.setPhones(request.getPhones().stream()
                    .map(phoneReq -> {
                        Phone phone = new Phone();
                        phone.setNumber(phoneReq.getNumber());
                        phone.setCityCode(phoneReq.getCityCode());
                        phone.setCountryCode(phoneReq.getCountryCode());
                        phone.setUser(user); // Set the relationship from Phone back to User
                        return phone;
                    })
                    .collect(Collectors.toList()));
        }

        // 5. Save User (this will cascade save phones due to CascadeType.ALL)
        User savedUser = userRepository.save(user);

        // 6. Generate JWT Token (use the generated user ID)
        String token = jwtTokenUtil.generateToken(savedUser.getId());
        savedUser.setToken(token); // Store the token

        // Save again to persist the token
        savedUser = userRepository.save(savedUser);

        // 7. Map Saved Entity back to Response DTO
        CreateUserResponse response = new CreateUserResponse();
        response.setId(savedUser.getId());
        response.setCreated(savedUser.getCreated());
        response.setModified(savedUser.getModified());
        response.setLastLogin(savedUser.getLastLogin());
        response.setToken(savedUser.getToken());
        response.setActive(savedUser.isActive());

        return response;
    }

    // Implement other service methods (getUserById, updateUser, etc.) here
}