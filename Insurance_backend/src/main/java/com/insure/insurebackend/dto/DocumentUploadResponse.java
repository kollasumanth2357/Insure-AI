package com.insure.insurebackend.dto;

import lombok.Data;

@Data
public class DocumentUploadResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    private String status;
}
