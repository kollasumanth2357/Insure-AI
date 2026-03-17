package com.insure.insurebackend.dto;

import lombok.Data;

@Data
public class AgentByPincodeResponse {
    private Long id;
    private String name;
    private String pincode;
    private Double latitude;
    private Double longitude;
}
