package com.insure.insurebackend.service;

import com.insure.insurebackend.model.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BankOtpService {

    private static class PendingAccount {
        String bankName;
        String accountNumber;
        String ifscCode;
        String otp;
        Instant expiresAt;
    }

    private final Map<String, PendingAccount> pending = new ConcurrentHashMap<>();
    private final BankAccountService bankAccountService;
    private final Random random = new Random();

    public BankOtpService(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public String createOtp(User user,
                            String bankName,
                            String accountNumber,
                            String ifscCode) {

        PendingAccount p = new PendingAccount();
        p.bankName = bankName;
        p.accountNumber = accountNumber;
        p.ifscCode = ifscCode;
        p.otp = String.format("%06d", random.nextInt(1_000_000));
        p.expiresAt = Instant.now().plus(Duration.ofMinutes(5));

        pending.put(user.getUsername(), p);
        return p.otp;
    }

    public void clear(User user) {
        pending.remove(user.getUsername());
    }

    public com.insure.insurebackend.model.BankAccount verifyAndSave(User user, String otp) {
        PendingAccount p = pending.get(user.getUsername());

        if (p == null) {
            throw new RuntimeException("No pending bank verification found");
        }

        if (Instant.now().isAfter(p.expiresAt)) {
            pending.remove(user.getUsername());
            throw new RuntimeException("OTP has expired");
        }

        if (!p.otp.equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        pending.remove(user.getUsername());
        return bankAccountService.saveAccount(user, p.bankName, p.accountNumber, p.ifscCode);
    }
}

