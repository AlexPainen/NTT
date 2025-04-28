package com.ntt.userapi.controller;

import com.ntt.userapi.model.dto.CreateUserRequest;
import com.ntt.userapi.model.dto.CreateUserResponse;
import com.ntt.userapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Marks the class as a REST Controller
@RequestMapping("/api/users") // Base path for user endpoints
public class UserController {

    private final UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to create a new user.
     *
     * @param request The user data for creation.
     * @return The response containing the created user's details.
     */
    @PostMapping // Handles POST requests to /api/users
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        // @Valid triggers the bean validation annotations in CreateUserRequest
        // @RequestBody binds the JSON request body to the DTO

        CreateUserResponse response = userService.createUser(request);

        // Return 201 Created status with the response body
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Add other endpoints (GET, PUT, PATCH, DELETE) here following similar patterns
    // @GetMapping("/{id}") ...
    // @GetMapping ...
    // @PutMapping("/{id}") ...
    // @PatchMapping("/{id}") ...
    // @DeleteMapping("/{id}") ...
}