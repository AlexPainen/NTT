package com.ntt.userapi.service;

import com.ntt.userapi.dto.PhoneRequestDTO;
import com.ntt.userapi.dto.UserRequestDTO;
import com.ntt.userapi.dto.UserResponseDTO;
import com.ntt.userapi.exception.EmailAlreadyExistsException;
import com.ntt.userapi.exception.InvalidPasswordFormatException;
import com.ntt.userapi.model.User;
import com.ntt.userapi.repository.UserRepository;
import com.ntt.userapi.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequestDTO;
    private User user;
    private final UUID userId = UUID.randomUUID();
    private final String token = "jwt-token";

    @BeforeEach
    void setUp() {
        // Configurar regex de contraseña para pruebas
        ReflectionTestUtils.setField(userService, "passwordRegex", "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

        // Preparar datos de prueba
        PhoneRequestDTO phoneRequestDTO = new PhoneRequestDTO("1234567", "1", "57");

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNombre("Juan Rodriguez");
        userRequestDTO.setCorreo("juan@rodriguez.org");
        userRequestDTO.setPassword("Password@123");
        userRequestDTO.setTelefonos(Arrays.asList(phoneRequestDTO));

        LocalDateTime now = LocalDateTime.now();

        user = User.builder()
                .id(userId)
                .name(userRequestDTO.getNombre())
                .email(userRequestDTO.getCorreo())
                .password(userRequestDTO.getPassword())
                .created(now)
                .lastLogin(now)
                .token(token)
                .active(true)
                .build();
    }

    @Test
    void whenCreateUser_thenSuccess() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(jwtTokenProvider.generateToken(anyString())).thenReturn(token);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponseDTO result = userService.createUser(userRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(token, result.getToken());
        assertTrue(result.isActivo());

        verify(userRepository).existsByEmail(userRequestDTO.getCorreo());
        verify(jwtTokenProvider).generateToken(userRequestDTO.getCorreo());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void whenCreateUserWithExistingEmail_thenThrowEmailAlreadyExistsException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(userRequestDTO);
        });

        verify(userRepository).existsByEmail(userRequestDTO.getCorreo());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenCreateUserWithInvalidPassword_thenThrowInvalidPasswordFormatException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        userRequestDTO.setPassword("simple"); // Contraseña que no cumple con el regex

        // When & Then
        assertThrows(InvalidPasswordFormatException.class, () -> {
            userService.createUser(userRequestDTO);
        });

        verify(userRepository).existsByEmail(userRequestDTO.getCorreo());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenGetUserById_thenSuccess() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserResponseDTO result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(token, result.getToken());

        verify(userRepository).findById(userId);
    }
}