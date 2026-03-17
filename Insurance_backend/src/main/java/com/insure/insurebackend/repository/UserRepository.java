package com.insure.insurebackend.repository;

import com.insure.insurebackend.dto.AgentDirectoryResponse;
import com.insure.insurebackend.model.AgentSpecialization;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    long countByRole(Role role);

    @Query("select u from User u where u.role = :role")
    java.util.List<User> findByRole(@Param("role") Role role);

    @Query("select u from User u where u.role = com.insure.insurebackend.model.Role.ADMIN")
    java.util.List<User> findAdmins();

    @Query("select u from User u where u.role = com.insure.insurebackend.model.Role.CUSTOMER")
    java.util.List<User> findCustomers();

    @Query("select u from User u where u.role = com.insure.insurebackend.model.Role.AGENT")
    java.util.List<User> findAgents();

    @Query("""
            select new com.insure.insurebackend.dto.AgentDirectoryResponse(
                u.id,
                ap.id,
                u.fullName,
                u.username,
                u.email,
                u.phone,
                case
                    when ap.specialization is null then com.insure.insurebackend.model.AgentSpecialization.GENERAL_INSURANCE
                    else ap.specialization
                end,
                coalesce(ap.status, 'ACTIVE')
            )
            from User u
            left join AgentProfile ap on ap.user = u
            where u.role = :role
            """)
    java.util.List<AgentDirectoryResponse> findAgentDirectory(@Param("role") Role role);
}
