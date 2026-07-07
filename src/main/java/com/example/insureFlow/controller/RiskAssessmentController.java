package com.example.insureFlow.controller;

import com.example.insureFlow.dto.request.RiskAssessmentRequestDTO;
import com.example.insureFlow.dto.response.RiskAssessmentResponseDTO;
import com.example.insureFlow.service.RiskAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    @PostMapping
    @PreAuthorize("hasRole('RISK_ASSESSOR')")
    public ResponseEntity<RiskAssessmentResponseDTO> createAssessment(
            @Valid @RequestBody RiskAssessmentRequestDTO request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(riskAssessmentService.createAssessment(request, authentication.getName()));
    }

    @GetMapping("/policy/{policyId}")
    @PreAuthorize("hasAnyRole('UNDERWRITER', 'RISK_ASSESSOR', 'INSURANCE_MANAGER')")
    public ResponseEntity<List<RiskAssessmentResponseDTO>> getAssessmentsForPolicy(@PathVariable Long policyId) {
        return ResponseEntity.ok(riskAssessmentService.getAssessmentsForPolicy(policyId));
    }
}