package com.santhosh.library.dto;

import com.santhosh.library.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRoleRequest {
    @Email
    @NotBlank(message = "Email is required")
    private String email;
    @NotNull(message = "Role is required")
    private Role role;
}
