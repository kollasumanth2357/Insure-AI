package com.insure.insurebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentSummaryResponse {
    private Long successCount;
    private Long failedCount;
    private Long pendingCount;
    private Double totalSuccessAmount;
}
