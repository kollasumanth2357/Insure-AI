package com.insure.insurebackend.controller;

import com.insure.insurebackend.model.*;
import com.insure.insurebackend.service.*;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final AddressService addressService;
    private final FileStorageService fileStorageService;
    private final DocumentService documentService;
    private final BankOtpService bankOtpService;
    private final BankAccountService bankAccountService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ProfileController(UserService userService,
                             AddressService addressService,
                             FileStorageService fileStorageService,
                             DocumentService documentService,
                             BankOtpService bankOtpService,
                             BankAccountService bankAccountService) {

        this.userService = userService;
        this.addressService = addressService;
        this.fileStorageService = fileStorageService;
        this.documentService = documentService;
        this.bankOtpService = bankOtpService;
        this.bankAccountService = bankAccountService;
    }

    // ================= FETCH PROFILE =================
    @GetMapping
    public ProfileResponse getProfile(Authentication authentication) {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressService.findByUser(user).orElse(null);

        ProfileResponse response = new ProfileResponse();

        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setProfileImage(user.getProfileImage());

        if (address != null) {
            response.setDoorNo(address.getDoorNo());
            response.setBuildingName(address.getBuildingName());
            response.setStreet(address.getStreet());
            response.setArea(address.getArea());
            response.setCity(address.getCity());
            response.setDistrict(address.getDistrict());
            response.setState(address.getState());
            response.setPincode(address.getPincode());
        }

        return response;
    }

    // ================= UPDATE PROFILE =================
    @PutMapping
    public String updateProfile(@Valid @RequestBody ProfileRequest request,
                                Authentication authentication) {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        userService.save(user);
        addressService.saveOrUpdateAddress(user, request);

        return "Profile updated successfully";
    }

    // ================= CHANGE PASSWORD =================
    @PutMapping("/change-password")
    public String changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                 Authentication authentication) {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        userService.changePassword(
                user,
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return "Password updated successfully";
    }

    // ================= PROFILE IMAGE UPLOAD =================
    @PostMapping("/upload-image")
    public String uploadProfileImage(@RequestParam("file") MultipartFile file,
                                     Authentication authentication) throws Exception {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileName =
                fileStorageService.saveProfileImage(file, username);

        user.setProfileImage(fileName);
        userService.save(user);

        return "Profile image uploaded successfully";
    }

    // ================= DOCUMENT UPLOAD =================
    @PostMapping("/upload-document")
    public String uploadDocument(@RequestParam("file") MultipartFile file,
                                 Authentication authentication) throws Exception {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileName =
                fileStorageService.saveDocument(file, username);

        documentService.saveDocument(
                user,
                fileName,
                file.getContentType(),
                file.getSize()
        );

        return "Document uploaded successfully";
    }

    // ================= GET USER DOCUMENTS =================
    @GetMapping("/documents")
    public List<Document> getUserDocuments(Authentication authentication) {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return documentService.getUserDocuments(user);
    }

    // ================= DELETE DOCUMENT =================
    @DeleteMapping("/documents/{id}")
    public String deleteDocument(@PathVariable Long id,
                                 Authentication authentication) throws Exception {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = documentService.getDocumentById(id);

        if (!document.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized document access");
        }

        fileStorageService.deleteDocumentFile(document.getFileName());
        documentService.deleteDocument(document);

        return "Document deleted successfully";
    }

    // ================= DOWNLOAD DOCUMENT =================
    @GetMapping("/documents/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id,
                                                     Authentication authentication) {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = documentService.getDocumentById(id);

        if (!document.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized document access");
        }

        try {
            Path filePath = Paths
                    .get(uploadDir)
                    .resolve("documents")
                    .resolve(document.getFileName())
                    .normalize();

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + document.getFileName() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ================= BANK ACCOUNT – SEND OTP =================
    @PostMapping("/bank/send-otp")
    public java.util.Map<String, String> sendBankOtp(@RequestBody BankAccountRequest request,
                                                     Authentication authentication) {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = bankOtpService.createOtp(
                user,
                request.getBankName(),
                request.getAccountNumber(),
                request.getIfscCode()
        );

        // In a real system, the OTP would be sent via SMS. For this demo we return it in the response.
        return java.util.Map.of(
                "message", "OTP sent to your registered mobile number (demo)",
                "otp", otp
        );
    }

    // ================= BANK ACCOUNT – VERIFY OTP & SAVE =================
    public record OtpRequest(String otp) {}

    @PostMapping("/bank/verify-otp")
    public BankAccountResponse verifyBankOtp(@RequestBody OtpRequest request,
                                             Authentication authentication) {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        com.insure.insurebackend.model.BankAccount account =
                bankOtpService.verifyAndSave(user, request.otp());

        return BankAccountResponse.fromEntity(account);
    }

    // ================= BANK ACCOUNT – FETCH LINKED ACCOUNT =================
    @GetMapping("/bank-account")
    public BankAccountResponse getBankAccount(Authentication authentication) {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bankAccountService.findByUser(user)
                .map(BankAccountResponse::fromEntity)
                .orElse(null);
    }
}
