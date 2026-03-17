package com.insure.insurebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LatLngResponse {

    private final Double latitude;
    private final Double longitude;
}
