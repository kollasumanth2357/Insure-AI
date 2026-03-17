package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.PaymentConfirmRequest;
import com.insure.insurebackend.dto.PaymentConfirmResponse;
import com.insure.insurebackend.dto.PaymentOrderRequest;
import com.insure.insurebackend.dto.PaymentOrderResponse;
import com.insure.insurebackend.service.PaymentOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentOrderController {

    private final PaymentOrderService paymentOrderService;

    public PaymentOrderController(PaymentOrderService paymentOrderService) {
        this.paymentOrderService = paymentOrderService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@Valid @RequestBody PaymentOrderRequest request,
                                                            Authentication authentication) {
        return ResponseEntity.ok(paymentOrderService.createOrder(authentication.getName(), request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirm(@Valid @RequestBody PaymentConfirmRequest request,
                                                          Authentication authentication) {
        return ResponseEntity.ok(paymentOrderService.confirmPayment(authentication.getName(), request));
    }
}
