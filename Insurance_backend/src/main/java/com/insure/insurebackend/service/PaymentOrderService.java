package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.CustomerPolicyRequest;
import com.insure.insurebackend.dto.PaymentConfirmRequest;
import com.insure.insurebackend.dto.PaymentConfirmResponse;
import com.insure.insurebackend.dto.PaymentOrderRequest;
import com.insure.insurebackend.dto.PaymentOrderResponse;
import com.insure.insurebackend.model.CustomerPolicy;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.CustomerPolicyRepository;
import com.insure.insurebackend.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentOrderService {

    @Value("${razorpay.key.id:rzp_test_demo_key}")
    private String razorpayKeyId;

    private final UserService userService;
    private final PolicyRepository policyRepository;
    private final CustomerPolicyService customerPolicyService;
    private final CustomerPolicyRepository customerPolicyRepository;

    public PaymentOrderService(UserService userService,
                               PolicyRepository policyRepository,
                               CustomerPolicyService customerPolicyService,
                               CustomerPolicyRepository customerPolicyRepository) {
        this.userService = userService;
        this.policyRepository = policyRepository;
        this.customerPolicyService = customerPolicyService;
        this.customerPolicyRepository = customerPolicyRepository;
    }

    public PaymentOrderResponse createOrder(String username, PaymentOrderRequest request) {
        User user = getCustomer(username);
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        if (customerPolicyRepository.existsByCustomerIdAndPolicyId(user.getId(), policy.getId())) {
            throw new IllegalArgumentException("Policy already purchased");
        }
        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setOrderId("order_" + UUID.randomUUID().toString().replace("-", ""));
        response.setKey(razorpayKeyId);
        response.setAmount(request.getAmount());
        response.setCurrency("INR");
        response.setPolicyName(policy.getName());
        return response;
    }

    public PaymentConfirmResponse confirmPayment(String username, PaymentConfirmRequest request) {
        User user = getCustomer(username);
        if (customerPolicyRepository.existsByCustomerIdAndPolicyId(user.getId(), request.getPolicyId())) {
            throw new IllegalArgumentException("Policy already purchased");
        }
        CustomerPolicyRequest purchaseRequest = new CustomerPolicyRequest();
        purchaseRequest.setPolicyId(request.getPolicyId());
        purchaseRequest.setAmount(request.getAmount());
        CustomerPolicy customerPolicy = customerPolicyService.purchasePolicy(user.getId(), purchaseRequest);
        return new PaymentConfirmResponse("Payment confirmed and policy purchased", customerPolicy.getId());
    }

    private User getCustomer(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can pay for policies");
        }
        return user;
    }
}
