package com.insure.insurebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AvailabilityUpdateRequest {
    @NotBlank
    private String availabilityStatus;
}
