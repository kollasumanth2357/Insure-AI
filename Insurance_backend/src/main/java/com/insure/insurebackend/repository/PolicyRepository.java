package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    @Query("select p from Policy p where coalesce(p.isDeleted, false) = false and p.status = 'ACTIVE'")
    List<Policy> findActivePolicies();

    @Query("select p from Policy p")
    List<Policy> findAllPolicies();

    @Query("select count(p) from Policy p where coalesce(p.isDeleted, false) = false and p.status = 'ACTIVE'")
    long countActivePolicies();

    List<Policy> findByStatus(String status);
}
