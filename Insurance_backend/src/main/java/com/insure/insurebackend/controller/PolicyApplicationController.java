package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.*;
import com.insure.insurebackend.service.PolicyApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/policy")
@CrossOrigin(origins = "http://localhost:5173")
public class PolicyApplicationController {

    private final PolicyApplicationService policyApplicationService;

    public PolicyApplicationController(PolicyApplicationService policyApplicationService) {
        this.policyApplicationService = policyApplicationService;
    }

    @PostMapping("/apply/start")
    public ResponseEntity<PolicyApplyStartResponse> start(@Valid @RequestBody PolicyApplyStartRequest request,
                                                          Authentication authentication) {
        return ResponseEntity.ok(
                policyApplicationService.startApplication(authentication.getName(), request.getPolicyId())
        );
    }

    @PostMapping("/apply/send-otp")
    public ResponseEntity<PolicyOtpSendResponse> sendOtp(@Valid @RequestBody PolicyOtpSendRequest request,
                                                         Authentication authentication) {
        return ResponseEntity.ok(
                policyApplicationService.sendOtp(authentication.getName(), request.getPhone())
        );
    }

    @PostMapping("/apply/verify-otp")
    public ResponseEntity<PolicyOtpVerifyResponse> verifyOtp(@Valid @RequestBody PolicyOtpVerifyRequest request,
                                                             Authentication authentication) {
        return ResponseEntity.ok(
                policyApplicationService.verifyOtp(authentication.getName(), request.getPhone(), request.getOtp())
        );
    }

    @GetMapping("/documents/{policyId}")
    public ResponseEntity<List<PolicyRequiredDocumentResponse>> requiredDocuments(@PathVariable Long policyId) {
        return ResponseEntity.ok(policyApplicationService.getRequiredDocuments(policyId));
    }

    @PostMapping(value = "/upload-doc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PolicyDocumentUploadResponse> uploadDocument(@RequestParam Long policyId,
                                                                      @RequestParam String documentKey,
                                                                      @RequestParam(required = false) Long existingDocumentId,
                                                                      @RequestPart(required = false) MultipartFile file,
                                                                      Authentication authentication) throws Exception {
        return ResponseEntity.ok(
                policyApplicationService.uploadDocument(
                        authentication.getName(),
                        policyId,
                        documentKey,
                        existingDocumentId,
                        file
                )
        );
    }

    @PostMapping("/payment")
    public ResponseEntity<PolicyPaymentResponse> payment(@Valid @RequestBody PolicyPaymentRequest request,
                                                         Authentication authentication) {
        return ResponseEntity.ok(
                policyApplicationService.processPayment(authentication.getName(), request)
        );
    }
}
