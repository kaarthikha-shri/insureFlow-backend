package com.example.insureFlow.controller;

import com.example.insureFlow.dto.request.PolicyCreateRequestDTO;
import com.example.insureFlow.dto.response.PolicyResponseDTO;
import com.example.insureFlow.service.PolicyUnderWritingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyUnderWritingService policyUnderWritingService;

    @PostMapping
    @PreAuthorize("hasRole('UNDERWRITER')")
    public ResponseEntity<PolicyResponseDTO> createPolicy( @Valid @RequestBody PolicyCreateRequestDTO request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(policyUnderWritingService.createPolicy(request, authentication.getName()));
    }

    @PutMapping("/{policyId}/activate")
    @PreAuthorize("hasRole('UNDERWRITER')")
    public ResponseEntity<PolicyResponseDTO> activatePolicy(@PathVariable Long policyId) {
        return ResponseEntity.ok(policyUnderWritingService.activatePolicy(policyId));
    }

    @GetMapping("/my-policies")
    @PreAuthorize("hasRole('POLICYHOLDER')")
    public ResponseEntity<List<PolicyResponseDTO>> getMyPolicies(Authentication authentication) {
        return ResponseEntity.ok(policyUnderWritingService.getPoliciesForAccount(authentication.getName()));
    }

    @GetMapping("/pending-assessment")
    @PreAuthorize("hasAnyRole('UNDERWRITER', 'RISK_ASSESSOR')")
    public ResponseEntity<List<PolicyResponseDTO>> getPendingAssessment() {
        return ResponseEntity.ok(policyUnderWritingService.getPoliciesPendingAssessment());
    }

    @GetMapping
    @PreAuthorize("hasRole('INSURANCE_MANAGER')")
    public ResponseEntity<List<PolicyResponseDTO>> getAllPolicies() {
        return ResponseEntity.ok(policyUnderWritingService.getAllPolicies());
    }
}