package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.DocumentUploadResponse;
import com.insure.insurebackend.dto.UserDocumentResponse;
import com.insure.insurebackend.model.Document;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.service.DocumentService;
import com.insure.insurebackend.service.PolicyApplicationFileStorageService;
import com.insure.insurebackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class UserDocumentController {

    private final UserService userService;
    private final DocumentService documentService;
    private final PolicyApplicationFileStorageService fileStorageService;

    public UserDocumentController(UserService userService,
                                  DocumentService documentService,
                                  PolicyApplicationFileStorageService fileStorageService) {
        this.userService = userService;
        this.documentService = documentService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/api/user/documents")
    public ResponseEntity<List<UserDocumentResponse>> getDocuments(Authentication authentication) {
        User user = getUser(authentication.getName());
        return ResponseEntity.ok(documentService.getUserDocuments(user).stream().map(this::toResponse).toList());
    }

    @PostMapping("/api/documents/upload")
    public ResponseEntity<DocumentUploadResponse> upload(@RequestParam("file") MultipartFile file,
                                                         Authentication authentication) throws Exception {
        User user = getUser(authentication.getName());
        String fileName = fileStorageService.saveDocument(file, user.getUsername());
        documentService.saveDocument(user, fileName, file.getContentType(), file.getSize());
        Document document = documentService.getUserDocuments(user).stream()
                .filter(item -> fileName.equals(item.getFileName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Uploaded document not found"));

        DocumentUploadResponse response = new DocumentUploadResponse();
        response.setId(document.getId());
        response.setFileName(document.getFileName());
        response.setFileType(document.getFileType());
        response.setFileSize(document.getFileSize());
        response.setFileUrl("/documents/" + document.getFileName());
        response.setStatus("UPLOADED");
        return ResponseEntity.ok(response);
    }

    private UserDocumentResponse toResponse(Document document) {
        UserDocumentResponse response = new UserDocumentResponse();
        response.setId(document.getId());
        response.setFileName(document.getFileName());
        response.setFileType(document.getFileType());
        response.setFileSize(document.getFileSize());
        response.setFileUrl("/documents/" + document.getFileName());
        return response;
    }

    private User getUser(String username) {
        return userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
