package com.insure.insurebackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // ================= PROFILE IMAGE SAVE =================
    public String saveProfileImage(MultipartFile file, String username) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        String profileDir = uploadDir + File.separator + "profile";

        File directory = new File(profileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName =
                username + "_" + System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(profileDir, fileName);

        Files.copy(file.getInputStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    // ================= DOCUMENT SAVE =================
    public String saveDocument(MultipartFile file, String username) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size must be less than 5MB");
        }

        String documentDir = uploadDir + File.separator + "documents";

        File directory = new File(documentDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName =
                username + "_" + System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(documentDir, fileName);

        Files.copy(file.getInputStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    // ================= DELETE DOCUMENT FILE =================
    public void deleteDocumentFile(String fileName) throws IOException {

        String documentDir = uploadDir + File.separator + "documents";
        Path filePath = Paths.get(documentDir, fileName);

        Files.deleteIfExists(filePath);
    }
}