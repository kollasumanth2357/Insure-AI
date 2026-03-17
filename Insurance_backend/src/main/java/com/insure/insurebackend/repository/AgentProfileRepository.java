package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.AgentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgentProfileRepository extends JpaRepository<AgentProfile, Long> {
    Optional<AgentProfile> findByUserId(Long userId);

    List<AgentProfile> findByPincode(String pincode);

    List<AgentProfile> findByUserIdIn(List<Long> userIds);

    @Query("select a from AgentProfile a where (a.isDeleted = false or a.isDeleted is null)")
    List<AgentProfile> findAllNotDeleted();
}
