package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.PolicyApplicationProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyApplicationProgressRepository extends JpaRepository<PolicyApplicationProgress, Long> {

    Optional<PolicyApplicationProgress> findTopByUserIdAndPolicyIdOrderByUpdatedAtDesc(Long userId, Long policyId);

    Optional<PolicyApplicationProgress> findTopByUserIdOrderByUpdatedAtDesc(Long userId);
}
