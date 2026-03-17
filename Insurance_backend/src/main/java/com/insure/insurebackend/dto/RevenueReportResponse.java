package com.insure.insurebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueReportResponse {
    private String period;
    private Double amount;
}
