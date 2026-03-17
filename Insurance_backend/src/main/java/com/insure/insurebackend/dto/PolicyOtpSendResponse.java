package com.insure.insurebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PolicyOtpSendResponse {

    private String message;
    private String otp;
    private long expiresInSeconds;
}
