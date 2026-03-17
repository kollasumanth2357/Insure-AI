package com.insure.insurebackend.dto;

import lombok.Data;

@Data
public class PolicyDocumentUploadResponse {

    private String documentKey;
    private String label;
    private String status;
    private String reason;
    private String source;
    private Long documentId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
}
