package com.insure.insurebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PolicyRequiredDocumentResponse {

    private String key;
    private String label;
    private String description;
    private String acceptedTypes;
}
