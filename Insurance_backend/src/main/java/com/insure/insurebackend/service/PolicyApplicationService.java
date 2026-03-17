package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.*;
import com.insure.insurebackend.model.Address;
import com.insure.insurebackend.model.CustomerPolicy;
import com.insure.insurebackend.model.Document;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.DocumentRepository;
import com.insure.insurebackend.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PolicyApplicationService {

    private static final long OTP_EXPIRY_SECONDS = 45L;

    private final UserService userService;
    private final PolicyRepository policyRepository;
    private final AddressService addressService;
    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final PolicyApplicationFileStorageService fileStorageService;
    private final CustomerPolicyService customerPolicyService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<Long, OtpSession> otpSessions = new ConcurrentHashMap<>();

    public PolicyApplicationService(UserService userService,
                                    PolicyRepository policyRepository,
                                    AddressService addressService,
                                    DocumentService documentService,
                                    DocumentRepository documentRepository,
                                    PolicyApplicationFileStorageService fileStorageService,
                                    CustomerPolicyService customerPolicyService) {
        this.userService = userService;
        this.policyRepository = policyRepository;
        this.addressService = addressService;
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
        this.customerPolicyService = customerPolicyService;
    }

    public PolicyApplyStartResponse startApplication(String username, Long policyId) {
        User user = getCustomer(username);
        Policy policy = getActivePolicy(policyId);
        Address address = addressService.findByUser(user).orElse(null);

        PolicyApplyStartResponse response = new PolicyApplyStartResponse();
        response.setPolicyId(policy.getId());
        response.setPolicyName(policy.getName());
        response.setPolicyCategory(policy.getMainCategory());
        response.setPremiumAmount(policy.getPremiumAmount());
        response.setCoverageAmount(policy.getCoverageAmount());
        response.setBillingCycle(policy.getBillingCycle());
        response.setCustomerName(user.getFullName());
        response.setCustomerEmail(user.getEmail());
        response.setPhone(user.getPhone());

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

    public PolicyOtpSendResponse sendOtp(String username, String phone) {
        User user = getCustomer(username);
        String normalizedPhone = normalizePhone(phone);
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        otpSessions.put(user.getId(), new OtpSession(normalizedPhone, otp, LocalDateTime.now().plusSeconds(OTP_EXPIRY_SECONDS), false));
        return new PolicyOtpSendResponse("OTP sent to your mobile number (demo)", otp, OTP_EXPIRY_SECONDS);
    }

    public PolicyOtpVerifyResponse verifyOtp(String username, String phone, String otp) {
        User user = getCustomer(username);
        String normalizedPhone = normalizePhone(phone);
        OtpSession session = otpSessions.get(user.getId());
        if (session == null || !normalizedPhone.equals(session.phone())) {
            throw new IllegalArgumentException("OTP not requested for this mobile number");
        }
        if (LocalDateTime.now().isAfter(session.expiresAt())) {
            otpSessions.remove(user.getId());
            throw new IllegalArgumentException("OTP has expired. Please resend OTP");
        }
        if (!session.otp().equals(otp.trim())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        otpSessions.put(user.getId(), new OtpSession(session.phone(), session.otp(), session.expiresAt(), true));
        return new PolicyOtpVerifyResponse(true, "OTP verified successfully");
    }

    public List<PolicyRequiredDocumentResponse> getRequiredDocuments(Long policyId) {
        Policy policy = getActivePolicy(policyId);
        String category = policy.getMainCategory() == null ? "" : policy.getMainCategory().toUpperCase();
        List<PolicyRequiredDocumentResponse> documents = new ArrayList<>();
        documents.add(new PolicyRequiredDocumentResponse("IDENTITY_PROOF", "Identity Proof", "PAN card, Aadhaar card, or passport", "PDF/JPG/PNG"));
        documents.add(new PolicyRequiredDocumentResponse("ADDRESS_PROOF", "Address Proof", "Utility bill, Aadhaar card, or driving licence", "PDF/JPG/PNG"));

        if (category.contains("VEHICLE") || category.contains("CAR") || category.contains("BIKE")) {
            documents.add(new PolicyRequiredDocumentResponse("VEHICLE_RC", "Vehicle RC", "Registration certificate for the insured vehicle", "PDF/JPG/PNG"));
            documents.add(new PolicyRequiredDocumentResponse("DRIVING_LICENSE", "Driving Licence", "Valid driving licence of the proposer", "PDF/JPG/PNG"));
        } else if (category.contains("HEALTH")) {
            documents.add(new PolicyRequiredDocumentResponse("MEDICAL_REPORT", "Medical Report", "Recent medical report or declaration", "PDF/JPG/PNG"));
        } else if (category.contains("LIFE")) {
            documents.add(new PolicyRequiredDocumentResponse("INCOME_PROOF", "Income Proof", "Salary slip, bank statement, or IT return", "PDF/JPG/PNG"));
        } else if (category.contains("HOME")) {
            documents.add(new PolicyRequiredDocumentResponse("PROPERTY_PROOF", "Property Proof", "Property tax receipt or ownership proof", "PDF/JPG/PNG"));
        }

        return documents;
    }

    public PolicyDocumentUploadResponse uploadDocument(String username,
                                                       Long policyId,
                                                       String documentKey,
                                                       Long existingDocumentId,
                                                       MultipartFile file) throws Exception {
        User user = getCustomer(username);
        validateRequiredDocument(policyId, documentKey);

        if (existingDocumentId != null) {
            Document document = documentRepository.findById(existingDocumentId)
                    .orElseThrow(() -> new IllegalArgumentException("Selected profile document not found"));
            if (!document.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Unauthorized profile document selection");
            }
            return buildResponse(documentKey, resolveDocumentLabel(policyId, documentKey), "VERIFIED", null,
                    "PROFILE", document.getId(), document.getFileName(), document.getFileType(),
                    document.getFileSize(), "/documents/" + document.getFileName());
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a file or choose a profile document");
        }

        String fileName = fileStorageService.saveDocument(file, user.getUsername());
        documentService.saveDocument(user, fileName, file.getContentType(), file.getSize());
        Document savedDocument = documentService.getUserDocuments(user).stream()
                .filter(doc -> fileName.equals(doc.getFileName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Uploaded document not found"));

        return buildResponse(documentKey, resolveDocumentLabel(policyId, documentKey), "PENDING",
                "Document uploaded successfully and is pending verification",
                "UPLOAD", savedDocument.getId(), savedDocument.getFileName(), savedDocument.getFileType(),
                savedDocument.getFileSize(), "/documents/" + savedDocument.getFileName());
    }

    public PolicyPaymentResponse processPayment(String username, PolicyPaymentRequest request) {
        User user = getCustomer(username);
        Policy policy = getActivePolicy(request.getPolicyId());
        if (request.getSelectedDocuments() == null || request.getSelectedDocuments().isEmpty()) {
            throw new IllegalArgumentException("Please complete document verification before payment");
        }
        CustomerPolicyRequest purchaseRequest = new CustomerPolicyRequest();
        purchaseRequest.setPolicyId(policy.getId());
        purchaseRequest.setAmount(request.getAmount());
        CustomerPolicy customerPolicy = customerPolicyService.purchasePolicy(user.getId(), purchaseRequest);

        PolicyPaymentResponse response = new PolicyPaymentResponse();
        response.setMessage("Payment successful");
        response.setReceiptNumber("RCPT-" + System.currentTimeMillis());
        response.setCustomerPolicyId(customerPolicy.getId());
        response.setPolicyId(policy.getId());
        response.setPolicyName(policy.getName());
        response.setAmount(request.getAmount());
        response.setPaymentMethod(request.getPaymentMethod());
        response.setPaidAt(customerPolicy.getPurchaseDate());
        response.setSelectedDocuments(request.getSelectedDocuments());
        return response;
    }

    private User getCustomer(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can apply for policies");
        }
        return user;
    }

    private Policy getActivePolicy(Long policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        if (Boolean.TRUE.equals(policy.getIsDeleted()) || !"ACTIVE".equalsIgnoreCase(policy.getStatus())) {
            throw new IllegalArgumentException("Policy not found");
        }
        return policy;
    }

    private String normalizePhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            throw new IllegalArgumentException("Mobile number is required");
        }
        String normalized = phone.replaceAll("\\D", "");
        if (normalized.length() != 10) {
            throw new IllegalArgumentException("Please enter a valid 10-digit mobile number");
        }
        return normalized;
    }

    private void validateRequiredDocument(Long policyId, String documentKey) {
        boolean exists = getRequiredDocuments(policyId).stream()
                .anyMatch(doc -> doc.getKey().equalsIgnoreCase(documentKey));
        if (!exists) {
            throw new IllegalArgumentException("Invalid required document selection");
        }
    }

    private String resolveDocumentLabel(Long policyId, String documentKey) {
        return getRequiredDocuments(policyId).stream()
                .filter(doc -> doc.getKey().equalsIgnoreCase(documentKey))
                .map(PolicyRequiredDocumentResponse::getLabel)
                .findFirst()
                .orElse(documentKey);
    }

    private PolicyDocumentUploadResponse buildResponse(String documentKey,
                                                       String label,
                                                       String status,
                                                       String reason,
                                                       String source,
                                                       Long documentId,
                                                       String fileName,
                                                       String fileType,
                                                       Long fileSize,
                                                       String fileUrl) {
        PolicyDocumentUploadResponse response = new PolicyDocumentUploadResponse();
        response.setDocumentKey(documentKey);
        response.setLabel(label);
        response.setStatus(status);
        response.setReason(reason);
        response.setSource(source);
        response.setDocumentId(documentId);
        response.setFileName(fileName);
        response.setFileType(fileType);
        response.setFileSize(fileSize);
        response.setFileUrl(fileUrl);
        return response;
    }

    private record OtpSession(String phone, String otp, LocalDateTime expiresAt, boolean verified) {
    }
}
