package com.insure.insurebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLoginRequest {
    @NotBlank
    private String identifier;
    @NotBlank
    private String password;
    @NotBlank
    private String role;
}
