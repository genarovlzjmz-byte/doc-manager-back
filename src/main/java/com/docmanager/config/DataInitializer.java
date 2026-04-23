package com.docmanager.config;

import com.docmanager.entity.User;
import com.docmanager.enums.Role;
import com.docmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@docmanager.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrador")
                    .role(Role.ADMIN)
                    .active(true)
                    .deleted(false)
                    .createdBy("SYSTEM")
                    .build();
            userRepository.save(admin);
            log.info("Usuario admin creado: admin / admin123");
        }

        if (!userRepository.existsByUsername("editor")) {
            User editor = User.builder()
                    .username("editor")
                    .email("editor@docmanager.com")
                    .password(passwordEncoder.encode("editor123"))
                    .fullName("Editor de Documentos")
                    .role(Role.EDITOR)
                    .active(true)
                    .deleted(false)
                    .createdBy("SYSTEM")
                    .build();
            userRepository.save(editor);
            log.info("Usuario editor creado: editor / editor123");
        }

        if (!userRepository.existsByUsername("viewer")) {
            User viewer = User.builder()
                    .username("viewer")
                    .email("viewer@docmanager.com")
                    .password(passwordEncoder.encode("viewer123"))
                    .fullName("Visor de Documentos")
                    .role(Role.VIEWER)
                    .active(true)
                    .deleted(false)
                    .createdBy("SYSTEM")
                    .build();
            userRepository.save(viewer);
            log.info("Usuario viewer creado: viewer / viewer123");
        }
    }
}
