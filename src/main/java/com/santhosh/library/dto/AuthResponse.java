package com.santhosh.library.dto;

import com.santhosh.library.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String email;
    private String name;
    private Role role;

}
