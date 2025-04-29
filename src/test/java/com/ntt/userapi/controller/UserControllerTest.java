package com.ntt.userapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.userapi.dto.PhoneRequestDTO;
import com.ntt.userapi.dto.UserRequestDTO;
import com.ntt.userapi.dto.UserResponseDTO;
import com.ntt.userapi.exception.EmailAlreadyExistsException;
import com.ntt.userapi.exception.ResourceNotFoundException;
import com.ntt.userapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        PhoneRequestDTO phoneRequestDTO = new PhoneRequestDTO("1234567", "1", "57");

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNombre("Juan Rodriguez");
        userRequestDTO.setCorreo("juan@rodriguez.org");
        userRequestDTO.setPassword("Password@123");
        userRequestDTO.setTelefonos(Arrays.asList(phoneRequestDTO));

        LocalDateTime now = LocalDateTime.now();

        userResponseDTO = UserResponseDTO.builder()
                .id(userId)
                .creado(now)
                .ultimoLogin(now)
                .token("jwt-token")
                .activo(true)
                .build();
    }

    @Test
    void whenCreateUser_thenReturnsCreatedStatus() throws Exception {
        // Given
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userId.toString())))
                .andExpect(jsonPath("$.token", is("jwt-token")))
                .andExpect(jsonPath("$.activo", is(true)));
    }

    @Test
    void whenCreateUserWithExistingEmail_thenReturnsConflictStatus() throws Exception {
        // Given
        when(userService.createUser(any(UserRequestDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("El correo ya está registrado"));

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje", is("El correo ya está registrado")));
    }

    @Test
    void whenGetUserById_thenReturnsOkStatus() throws Exception {
        // Given
        when(userService.getUserById(eq(userId))).thenReturn(userResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.toString())))
                .andExpect(jsonPath("$.token", is("jwt-token")));
    }

    @Test
    void whenGetUserByNonExistingId_thenReturnsNotFoundStatus() throws Exception {
        // Given
        when(userService.getUserById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Usuario no encontrado")));
    }

    @Test
    void whenUpdateUser_thenReturnsOkStatus() throws Exception {
        // Given
        when(userService.updateUser(eq(userId), any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.toString())));
    }

    @Test
    void whenDeleteUser_thenReturnsNoContentStatus() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteNonExistingUser_thenReturnsNotFoundStatus() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Usuario no encontrado"))
                .when(userService).deleteUser(any(UUID.class));

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Usuario no encontrado")));
    }
}