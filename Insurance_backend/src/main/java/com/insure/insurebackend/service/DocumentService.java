package com.insure.insurebackend.service;

import com.insure.insurebackend.model.*;
import com.insure.insurebackend.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // ================= SAVE DOCUMENT =================
    public void saveDocument(User user, String fileName,
                             String fileType, Long fileSize) {

        Document document =
                new Document(fileName, fileType, fileSize, user);

        documentRepository.save(document);
    }

    // ================= GET USER DOCUMENTS =================
    public List<Document> getUserDocuments(User user) {
        return documentRepository.findByUser(user);
    }

    // ================= GET DOCUMENT BY ID =================
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    // ================= DELETE DOCUMENT =================
    public void deleteDocument(Document document) {
        documentRepository.delete(document);
    }
}