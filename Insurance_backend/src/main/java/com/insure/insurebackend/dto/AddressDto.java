package com.insure.insurebackend.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String doorNo;
    private String buildingName;
    private String street;
    private String area;
    private String city;
    private String district;
    private String state;
    private String pincode;
}
