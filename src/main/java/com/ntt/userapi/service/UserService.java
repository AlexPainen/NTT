package com.ntt.userapi.service;

import com.ntt.userapi.dto.PhoneRequestDTO;
import com.ntt.userapi.dto.UserRequestDTO;
import com.ntt.userapi.dto.UserResponseDTO;
import com.ntt.userapi.exception.EmailAlreadyExistsException;
import com.ntt.userapi.exception.InvalidPasswordFormatException;
import com.ntt.userapi.exception.ResourceNotFoundException;
import com.ntt.userapi.model.Phone;
import com.ntt.userapi.model.User;
import com.ntt.userapi.repository.UserRepository;
import com.ntt.userapi.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${password.regex}")
    private String passwordRegex;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        // Validar si el correo ya existe
        if (userRepository.existsByEmail(userRequestDTO.getCorreo())) {
            throw new EmailAlreadyExistsException("El correo ya está registrado");
        }

        // Validar formato de contraseña
        if (!userRequestDTO.getPassword().matches(passwordRegex)) {
            throw new InvalidPasswordFormatException("El formato de la contraseña es inválido");
        }

        // Generar token JWT
        String token = jwtTokenProvider.generateToken(userRequestDTO.getCorreo());

        // Mapear los teléfonos
        List<Phone> phones = mapPhones(userRequestDTO.getTelefonos());

        // Crear y persistir el usuario
        User user = User.builder()
                .name(userRequestDTO.getNombre())
                .email(userRequestDTO.getCorreo())
                .password(userRequestDTO.getPassword()) // TODO: encriptar password ?
                .phones(phones)
                .token(token)
                .build();

        User savedUser = userRepository.save(user);

        // Mapear y retornar la respuesta
        return UserResponseDTO.builder()
                .id(savedUser.getId())
                .creado(savedUser.getCreated())
                .modificado(savedUser.getModified())
                .ultimoLogin(savedUser.getLastLogin())
                .token(savedUser.getToken())
                .activo(savedUser.isActive())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return mapToUserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(UUID id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Validar si el nuevo correo ya existe y no es el mismo del usuario actual
        // TODO: Validar formato del correo
        if (!user.getEmail().equals(userRequestDTO.getCorreo()) &&
                userRepository.existsByEmail(userRequestDTO.getCorreo())) {
            throw new EmailAlreadyExistsException("El correo ya está registrado");
        }

        // Validar formato de contraseña si se está actualizando
        if (!userRequestDTO.getPassword().equals(user.getPassword()) &&
                !userRequestDTO.getPassword().matches(passwordRegex)) {
            throw new InvalidPasswordFormatException("El formato de la contraseña es inválido");
        }

        // Actualizar datos del usuario
        user.setName(userRequestDTO.getNombre());
        user.setEmail(userRequestDTO.getCorreo());
        user.setPassword(userRequestDTO.getPassword());

        // Actualizar teléfonos
        user.getPhones().clear();
        user.getPhones().addAll(mapPhones(userRequestDTO.getTelefonos()));

        // Generar nuevo token si el correo cambió
        if (!user.getEmail().equals(userRequestDTO.getCorreo())) {
            String token = jwtTokenProvider.generateToken(userRequestDTO.getCorreo());
            user.setToken(token);
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponseDTO(updatedUser);
    }

    @Transactional
    public UserResponseDTO patchUser(UUID id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Actualizar solo los campos no nulos
        if (userRequestDTO.getNombre() != null) {
            user.setName(userRequestDTO.getNombre());
        }

        if (userRequestDTO.getCorreo() != null) {
            // Validar si el nuevo correo ya existe y no es el mismo del usuario actual
            // TODO: Validar formato del correo
            if (!user.getEmail().equals(userRequestDTO.getCorreo()) &&
                    userRepository.existsByEmail(userRequestDTO.getCorreo())) {
                throw new EmailAlreadyExistsException("El correo ya está registrado");
            }

            user.setEmail(userRequestDTO.getCorreo());

            // Generar nuevo token si el correo cambió
            String token = jwtTokenProvider.generateToken(userRequestDTO.getCorreo());
            user.setToken(token);
        }

        if (userRequestDTO.getPassword() != null) {
            // Validar formato de contraseña
            if (!userRequestDTO.getPassword().matches(passwordRegex)) {
                throw new InvalidPasswordFormatException("El formato de la contraseña es inválido");
            }

            user.setPassword(userRequestDTO.getPassword());
        }

        if (userRequestDTO.getTelefonos() != null && !userRequestDTO.getTelefonos().isEmpty()) {
            user.getPhones().clear();
            user.getPhones().addAll(mapPhones(userRequestDTO.getTelefonos()));
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDTO login(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        // Actualizar último login
        user.setLastLogin(LocalDateTime.now());

        // Generar nuevo token
        String token = jwtTokenProvider.generateToken(email);
        user.setToken(token);

        User updatedUser = userRepository.save(user);
        return mapToUserResponseDTO(updatedUser);
    }

    private List<Phone> mapPhones(List<PhoneRequestDTO> phoneRequestDTOs) {
        return phoneRequestDTOs.stream()
                .map(dto -> Phone.builder()
                        .number(dto.getNumero())
                        .cityCode(dto.getCodigoCiudad())
                        .countryCode(dto.getCodigoPais())
                        .build())
                .collect(Collectors.toList());
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .creado(user.getCreated())
                .modificado(user.getModified())
                .ultimoLogin(user.getLastLogin())
                .token(user.getToken())
                .activo(user.isActive())
                .build();
    }
}