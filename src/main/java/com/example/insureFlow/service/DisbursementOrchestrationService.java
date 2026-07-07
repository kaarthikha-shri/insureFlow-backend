package com.example.insureFlow.service;

import com.example.insureFlow.dto.response.DisbursementResponseDTO;
import com.example.insureFlow.entity.ClaimDisbursement;
import com.example.insureFlow.entity.ClaimStatus;
import com.example.insureFlow.entity.ClaimSubmission;
import com.example.insureFlow.entity.DisbursementStatus;
import com.example.insureFlow.exception.BusinessValidationException;
import com.example.insureFlow.exception.ResourceNotFoundException;
import com.example.insureFlow.repository.ClaimDisbursementRepository;
import com.example.insureFlow.repository.ClaimSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisbursementOrchestrationService {

    private final ClaimDisbursementRepository claimDisbursementRepository;
    private final ClaimSubmissionRepository claimSubmissionRepository;

    
    @Transactional
    public DisbursementResponseDTO scheduleDisbursement(Long claimId) {
        ClaimSubmission claim = claimSubmissionRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("No claim found with id: " + claimId));

        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new BusinessValidationException(
                    "Only APPROVED claims can be scheduled for disbursement. Current status: " + claim.getStatus());
        }

        if (claim.getDisbursement() != null) {
            throw new BusinessValidationException("A disbursement already exists for this claim.");
        }

        ClaimDisbursement disbursement = new ClaimDisbursement();
        disbursement.setClaim(claim);
        disbursement.setDisbursedAmount(claim.getClaimedAmount());
        disbursement.setStatus(DisbursementStatus.SCHEDULED);

        ClaimDisbursement saved = claimDisbursementRepository.save(disbursement);
        return toResponseDTO(saved);
    }

    
    @Transactional
    public DisbursementResponseDTO completeDisbursement(Long disbursementId) {
        ClaimDisbursement disbursement = claimDisbursementRepository.findById(disbursementId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No disbursement found with id: " + disbursementId));

        if (disbursement.getStatus() != DisbursementStatus.SCHEDULED) {
            throw new BusinessValidationException(
                    "Only SCHEDULED disbursements can be completed. Current status: " + disbursement.getStatus());
        }

        disbursement.setStatus(DisbursementStatus.COMPLETED);
        disbursement.setCompletedAt(LocalDateTime.now());

        ClaimSubmission claim = disbursement.getClaim();
        claim.setStatus(ClaimStatus.DISBURSED);
        claimSubmissionRepository.save(claim);

        ClaimDisbursement saved = claimDisbursementRepository.save(disbursement);
        return toResponseDTO(saved);
    }

    public List<DisbursementResponseDTO> getDisbursementsByStatus(DisbursementStatus status) {
        return claimDisbursementRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private DisbursementResponseDTO toResponseDTO(ClaimDisbursement disbursement) {
        return DisbursementResponseDTO.builder()
                .id(disbursement.getId())
                .disbursedAmount(disbursement.getDisbursedAmount())
                .status(disbursement.getStatus())
                .scheduledAt(disbursement.getScheduledAt())
                .completedAt(disbursement.getCompletedAt())
                .claimId(disbursement.getClaim().getId())
                .build();
    }
}