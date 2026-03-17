package com.insure.insurebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PolicyOtpVerifyResponse {

    private boolean verified;
    private String message;
}
