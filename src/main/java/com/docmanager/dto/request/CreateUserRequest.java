package com.docmanager.dto.request;

import com.docmanager.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 100, message = "Username debe tener entre 3 y 100 caracteres")
    private String username;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "Nombre completo es requerido")
    private String fullName;

    @NotNull(message = "Rol es requerido")
    private Role role;
}
