package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.BankAccount;
import com.insure.insurebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findFirstByUser(User user);
}

