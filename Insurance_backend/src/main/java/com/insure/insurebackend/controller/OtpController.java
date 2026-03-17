package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.OtpSendRequest;
import com.insure.insurebackend.dto.OtpSendResponse;
import com.insure.insurebackend.dto.OtpVerifyRequest;
import com.insure.insurebackend.dto.OtpVerifyResponse;
import com.insure.insurebackend.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "http://localhost:5173")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<OtpSendResponse> send(@Valid @RequestBody OtpSendRequest request,
                                                Authentication authentication) {
        return ResponseEntity.ok(otpService.sendOtp(authentication.getName(), request.getMobile()));
    }

    @PostMapping("/verify")
    public ResponseEntity<OtpVerifyResponse> verify(@Valid @RequestBody OtpVerifyRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(otpService.verifyOtp(authentication.getName(), request.getMobile(), request.getOtp()));
    }
}
