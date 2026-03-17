package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByUserIdAndMobileOrderByIdDesc(Long userId, String mobile);
}
