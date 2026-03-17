package com.insure.insurebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PolicyApplyStartRequest {

    @NotNull
    private Long policyId;
}
