package com.insure.insurebackend.service;

import com.insure.insurebackend.model.BankAccount;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public BankAccount saveAccount(User user,
                                   String bankName,
                                   String accountNumber,
                                   String ifscCode) {

        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setBankName(bankName);
        account.setAccountNumber(accountNumber);
        account.setIfscCode(ifscCode);
        account.setVerified(true);
        account.setCreatedAt(LocalDateTime.now());

        return bankAccountRepository.save(account);
    }

    public Optional<BankAccount> findByUser(User user) {
        return bankAccountRepository.findFirstByUser(user);
    }
}

