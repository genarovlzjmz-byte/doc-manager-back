package com.docmanager.dto.request;

import com.docmanager.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Email(message = "Email inválido")
    private String email;
    private String fullName;
    private Role role;
    private Boolean active;
}
