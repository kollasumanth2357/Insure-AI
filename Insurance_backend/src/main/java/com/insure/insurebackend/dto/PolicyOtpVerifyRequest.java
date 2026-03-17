package com.insure.insurebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PolicyOtpVerifyRequest {

    @NotBlank
    private String phone;

    @NotBlank
    private String otp;
}
