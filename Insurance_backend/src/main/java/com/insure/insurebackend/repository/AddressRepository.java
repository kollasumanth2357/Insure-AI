package com.insure.insurebackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.insure.insurebackend.model.Address;
import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.model.User;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByUser(User user);

    Optional<Address> findByUserId(Long userId);

    @Query(value = """
            SELECT u.id AS id,
                   u.full_name AS name,
                   u.pincode AS pincode,
                   u.latitude AS latitude,
                   u.longitude AS longitude
            FROM users u
            WHERE u.role = 'AGENT'
              AND u.pincode = :pincode
            """, nativeQuery = true)
    List<AgentByPincodeView> findAgentsByPincode(@Param("pincode") String pincode);

    @Query("select a from Address a join fetch a.user u where u.role = :role and a.pincode = :pincode")
    List<Address> findAgentAddressesByPincode(@Param("pincode") String pincode, @Param("role") Role role);
}
