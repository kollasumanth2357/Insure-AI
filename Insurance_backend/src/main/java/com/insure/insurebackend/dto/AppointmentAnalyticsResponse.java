package com.insure.insurebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppointmentAnalyticsResponse {
    private Long total;
    private Long completed;
    private Long cancelled;
    private Long scheduled;
}
