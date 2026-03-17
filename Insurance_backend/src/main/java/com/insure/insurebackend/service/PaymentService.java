package com.insure.insurebackend.service;

import com.insure.insurebackend.model.Payment;
import com.insure.insurebackend.model.PaymentStatus;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment recordPayment(User customer, Policy policy, BigDecimal amount, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setPolicy(policy);
        payment.setAmount(amount);
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsForCustomer(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }
}
