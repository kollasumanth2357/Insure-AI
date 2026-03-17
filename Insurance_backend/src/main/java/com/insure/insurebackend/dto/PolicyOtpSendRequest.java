package com.insure.insurebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PolicyOtpSendRequest {

    @NotBlank
    private String phone;
}
