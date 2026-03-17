package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.OtpSendResponse;
import com.insure.insurebackend.dto.OtpVerifyResponse;
import com.insure.insurebackend.model.OtpVerification;
import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.OtpVerificationRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private static final long OTP_EXPIRY_SECONDS = 60L;

    private final UserService userService;
    private final OtpVerificationRepository otpVerificationRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(UserService userService, OtpVerificationRepository otpVerificationRepository) {
        this.userService = userService;
        this.otpVerificationRepository = otpVerificationRepository;
    }

    public OtpSendResponse sendOtp(String username, String mobile) {
        User user = getCustomer(username);
        String normalizedMobile = normalizeMobile(mobile);
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setUser(user);
        otpVerification.setMobile(normalizedMobile);
        otpVerification.setOtpCode(String.format("%06d", secureRandom.nextInt(1_000_000)));
        otpVerification.setExpiresAt(LocalDateTime.now().plusSeconds(OTP_EXPIRY_SECONDS));
        otpVerification.setVerified(false);
        otpVerificationRepository.save(otpVerification);
        return new OtpSendResponse("OTP sent successfully (mock SMS)", otpVerification.getOtpCode(), OTP_EXPIRY_SECONDS);
    }

    public OtpVerifyResponse verifyOtp(String username, String mobile, String otp) {
        User user = getCustomer(username);
        String normalizedMobile = normalizeMobile(mobile);
        OtpVerification otpVerification = otpVerificationRepository
                .findTopByUserIdAndMobileOrderByIdDesc(user.getId(), normalizedMobile)
                .orElseThrow(() -> new IllegalArgumentException("OTP not requested for this mobile number"));
        if (LocalDateTime.now().isAfter(otpVerification.getExpiresAt())) {
            throw new IllegalArgumentException("OTP expired. Please resend OTP");
        }
        if (!otpVerification.getOtpCode().equals(otp.trim())) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        otpVerification.setVerified(true);
        otpVerificationRepository.save(otpVerification);
        return new OtpVerifyResponse(true, "OTP verified successfully");
    }

    private User getCustomer(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can verify OTP");
        }
        return user;
    }

    private String normalizeMobile(String mobile) {
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile number is required");
        }
        String normalized = mobile.replaceAll("\\D", "");
        if (normalized.length() != 10) {
            throw new IllegalArgumentException("Enter a valid 10-digit mobile number");
        }
        return normalized;
    }
}
