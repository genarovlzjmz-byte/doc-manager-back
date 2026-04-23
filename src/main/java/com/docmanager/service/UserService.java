package com.docmanager.service;

import com.docmanager.dto.request.CreateUserRequest;
import com.docmanager.dto.request.UpdateUserRequest;
import com.docmanager.dto.response.UserResponse;
import com.docmanager.entity.User;
import com.docmanager.exception.AppExceptions;
import com.docmanager.mapper.UserMapper;
import com.docmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAllByDeletedFalse());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request, String createdBy) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppExceptions.DuplicateResourceException("Username ya existe: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppExceptions.DuplicateResourceException("Email ya existe: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedBy(createdBy);

        User saved = userRepository.save(user);
        log.info("Usuario creado: {} por {}", saved.getUsername(), createdBy);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User updated = userRepository.save(user);
        log.info("Usuario actualizado: {}", updated.getUsername());
        return userMapper.toResponse(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Usuario no encontrado con id: " + id));
        user.setDeleted(true);
        user.setActive(false);
        userRepository.save(user);
        log.info("Usuario eliminado (soft delete): {}", user.getUsername());
    }
}
