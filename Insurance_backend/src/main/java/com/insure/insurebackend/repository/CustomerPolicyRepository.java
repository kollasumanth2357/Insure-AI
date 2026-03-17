package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.CustomerPolicy;
import com.insure.insurebackend.model.CustomerPolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerPolicyRepository extends JpaRepository<CustomerPolicy, Long> {
    List<CustomerPolicy> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndPolicyId(Long customerId, Long policyId);

    long countByStatus(CustomerPolicyStatus status);

    @Modifying
    @Query(value = """
            update customer_policies cp
            join policies p on p.id = cp.policy_id
            set cp.start_date = curdate(),
                cp.end_date = date_add(curdate(), interval coalesce(p.duration_months, 0) month),
                cp.status = :status,
                cp.premium_amount = :premium
            where cp.id = :customerPolicyId
            """, nativeQuery = true)
    int populatePurchaseDetails(@Param("customerPolicyId") Long customerPolicyId,
                                @Param("status") String status,
                                @Param("premium") java.math.BigDecimal premium);
}
