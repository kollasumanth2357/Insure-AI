package com.insure.insurebackend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentOrderResponse {

    private String orderId;
    private String key;
    private BigDecimal amount;
    private String currency;
    private String policyName;
}
