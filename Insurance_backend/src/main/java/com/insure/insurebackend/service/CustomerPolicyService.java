package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.CustomerPolicyRequest;
import com.insure.insurebackend.model.CustomerPolicy;
import com.insure.insurebackend.model.CustomerPolicyStatus;
import com.insure.insurebackend.model.Payment;
import com.insure.insurebackend.model.PaymentStatus;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.CustomerPolicyRepository;
import com.insure.insurebackend.repository.PolicyRepository;
import com.insure.insurebackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerPolicyService {

    private final CustomerPolicyRepository customerPolicyRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    public CustomerPolicyService(CustomerPolicyRepository customerPolicyRepository,
                                 PolicyRepository policyRepository,
                                 UserRepository userRepository,
                                 PaymentService paymentService) {
        this.customerPolicyRepository = customerPolicyRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public CustomerPolicy purchasePolicy(Long customerId, CustomerPolicyRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        if (customer.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can purchase policies");
        }
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        if (Boolean.TRUE.equals(policy.getIsDeleted())) {
            throw new IllegalArgumentException("Policy not found");
        }
        if (!"ACTIVE".equalsIgnoreCase(policy.getStatus())) {
            throw new IllegalArgumentException("Policy not found");
        }

        Payment payment = paymentService.recordPayment(customer, policy, request.getAmount(), PaymentStatus.SUCCESS);
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalArgumentException("Payment failed");
        }

        CustomerPolicy customerPolicy = new CustomerPolicy();
        customerPolicy.setCustomer(customer);
        customerPolicy.setPolicy(policy);
        customerPolicy.setStatus(CustomerPolicyStatus.ACTIVE);

        CustomerPolicy saved = customerPolicyRepository.save(customerPolicy);
        customerPolicyRepository.populatePurchaseDetails(saved.getId(), CustomerPolicyStatus.ACTIVE.name(), request.getAmount());

        return saved;
    }

    public List<CustomerPolicy> getCustomerPolicies(Long customerId) {
        return customerPolicyRepository.findByCustomerId(customerId);
    }
}
