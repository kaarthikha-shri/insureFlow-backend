package com.example.insureFlow.controller;

import com.example.insureFlow.dto.response.DisbursementResponseDTO;
import com.example.insureFlow.service.DisbursementOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/disbursements")
@RequiredArgsConstructor
public class DisbursementController {

    private final DisbursementOrchestrationService disbursementOrchestrationService;

    @PostMapping("/claim/{claimId}/schedule")
    @PreAuthorize("hasRole('INSURANCE_MANAGER')")
    public ResponseEntity<DisbursementResponseDTO> scheduleDisbursement(@PathVariable Long claimId) {
        return ResponseEntity.ok(disbursementOrchestrationService.scheduleDisbursement(claimId));
    }

    @PutMapping("/{disbursementId}/complete")
    @PreAuthorize("hasRole('INSURANCE_MANAGER')")
    public ResponseEntity<DisbursementResponseDTO> completeDisbursement(@PathVariable Long disbursementId) {
        return ResponseEntity.ok(disbursementOrchestrationService.completeDisbursement(disbursementId));
    }
}