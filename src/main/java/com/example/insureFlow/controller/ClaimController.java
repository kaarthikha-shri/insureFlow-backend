package com.example.insureFlow.controller;

import com.example.insureFlow.dto.request.ClaimAdjudicationRequestDTO;
import com.example.insureFlow.dto.request.ClaimSubmitRequestDTO;
import com.example.insureFlow.dto.response.ClaimResponseDTO;
import com.example.insureFlow.service.ClaimProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimProcessingService claimProcessingService;

    
    @PostMapping
    @PreAuthorize("hasRole('POLICYHOLDER')")
    public ResponseEntity<ClaimResponseDTO> fileClaim(
            @Valid @RequestBody ClaimSubmitRequestDTO request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claimProcessingService.fileClaim(request, authentication.getName()));
    }

    
    @PutMapping("/{claimId}/adjudicate")
    @PreAuthorize("hasRole('INSURANCE_MANAGER')")
    public ResponseEntity<ClaimResponseDTO> adjudicateClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimAdjudicationRequestDTO request,
            Authentication authentication) {
        return ResponseEntity.ok(
                claimProcessingService.adjudicateClaim(claimId, request, authentication.getName()));
    }
    @GetMapping("/my-claims")
    @PreAuthorize("hasRole('POLICYHOLDER')")
    public ResponseEntity<List<ClaimResponseDTO>> getMyClaims(Authentication authentication) {
        return ResponseEntity.ok(claimProcessingService.getClaimsForAccount(authentication.getName()));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('INSURANCE_MANAGER')")
    public ResponseEntity<List<ClaimResponseDTO>> getPendingClaims() {
        return ResponseEntity.ok(claimProcessingService.getPendingClaims());
    }
}