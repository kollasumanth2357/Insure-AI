package com.insure.insurebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardKpiResponse {
    private Double revenueThisMonth;
    private Double conversionRate;
    private Double cancellationRate;
    private Double agentProductivity;
}
