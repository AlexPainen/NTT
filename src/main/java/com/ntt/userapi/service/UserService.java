package com.ntt.userapi.service;

import com.ntt.userapi.exception.EmailAlreadyExistsException;
import com.ntt.userapi.exception.InvalidEmailFormatException;
import com.ntt.userapi.exception.InvalidPasswordFormatException;
import com.ntt.userapi.model.dto.CreateUserRequest;
import com.ntt.userapi.model.dto.CreateUserResponse;

// Interface defining the contract for user service operations
public interface UserService {

    /**
     * Creates a new user based on the provided request.
     * Performs validation (email format, password format) and checks for email uniqueness.
     * Encodes password, generates JWT token, and persists user and phones.
     *
     * @param request The DTO containing user details for creation.
     * @return The DTO containing the created user's details and generated token.
     * @throws EmailAlreadyExistsException if the email already exists.
     * @throws InvalidEmailFormatException if the email format is invalid according to regex.
     * @throws InvalidPasswordFormatException if the password format is invalid according to regex.
     */
    CreateUserResponse createUser(CreateUserRequest request);

    // Add other methods here as you implement GET, PUT, PATCH, DELETE
    // UserResponse getUserById(UUID id);
    // List<UserResponse> getAllUsers();
    // UserResponse updateUser(UUID id, UpdateUserRequest request);
    // UserResponse patchUser(UUID id, PatchUserRequest request);
    // void deleteUser(UUID id);
}