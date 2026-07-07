package com.example.insureFlow.service;

import com.example.insureFlow.dto.request.ClaimAdjudicationRequestDTO;
import com.example.insureFlow.dto.request.ClaimSubmitRequestDTO;
import com.example.insureFlow.dto.response.ClaimResponseDTO;
import com.example.insureFlow.entity.*;
import com.example.insureFlow.exception.BusinessValidationException;
import com.example.insureFlow.exception.ResourceNotFoundException;
import com.example.insureFlow.repository.ClaimSubmissionRepository;
import com.example.insureFlow.repository.InsurancePolicyRepository;
import com.example.insureFlow.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimProcessingService {

    private final ClaimSubmissionRepository claimSubmissionRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final SystemAccountRepository systemAccountRepository;

    
    @Transactional
    public ClaimResponseDTO fileClaim(ClaimSubmitRequestDTO request, String claimantEmail) {
        InsurancePolicy policy = insurancePolicyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No policy found with id: " + request.getPolicyId()));

        SystemAccount claimant = systemAccountRepository.findByEmail(claimantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found."));

        if (!policy.getAccountHolder().getId().equals(claimant.getId())) {
            throw new BusinessValidationException("You can only file claims against your own policies.");
        }

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new BusinessValidationException(
                    "Claims can only be filed against ACTIVE policies. Current status: " + policy.getStatus());
        }

        if (request.getClaimedAmount().compareTo(policy.getSumInsured()) > 0) {
            throw new BusinessValidationException(
                    "Claimed amount cannot exceed the policy's sum insured (" + policy.getSumInsured() + ").");
        }

        ClaimSubmission claim = new ClaimSubmission();
        claim.setPolicy(policy);
        claim.setClaimant(claimant);
        claim.setReason(request.getReason());
        claim.setClaimedAmount(request.getClaimedAmount());
        claim.setStatus(ClaimStatus.SUBMITTED);

        ClaimSubmission saved = claimSubmissionRepository.save(claim);
        return toResponseDTO(saved);
    }

    
    @Transactional
    public ClaimResponseDTO adjudicateClaim(Long claimId, ClaimAdjudicationRequestDTO request, String managerEmail) {
        ClaimSubmission claim = claimSubmissionRepository.findByIdForUpdate(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("No claim found with id: " + claimId));

        SystemAccount manager = systemAccountRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager account not found."));

        if (claim.getStatus() != ClaimStatus.SUBMITTED && claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new BusinessValidationException(
                    "This claim has already been adjudicated. Current status: " + claim.getStatus());
        }

        if (request.getDecision() != ClaimStatus.APPROVED && request.getDecision() != ClaimStatus.REJECTED) {
            throw new BusinessValidationException("Decision must be either APPROVED or REJECTED.");
        }

        claim.setStatus(request.getDecision());
        claim.setAdjudicatedBy(manager);

        ClaimSubmission saved = claimSubmissionRepository.save(claim);
        return toResponseDTO(saved);
    }

    public ClaimResponseDTO getClaimById(Long claimId) {
        ClaimSubmission claim = claimSubmissionRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("No claim found with id: " + claimId));
        return toResponseDTO(claim);
    }

    public List<ClaimResponseDTO> getClaimsForClaimant(Long claimantId) {
        return claimSubmissionRepository.findByClaimant_Id(claimantId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ClaimResponseDTO> getClaimsForAccount(String email) {
        SystemAccount account = systemAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for email: " + email));
        return getClaimsForClaimant(account.getId());
    }

    public List<ClaimResponseDTO> getClaimsByStatus(ClaimStatus status) {
        return claimSubmissionRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    
    public List<ClaimResponseDTO> getPendingClaims() {
        List<ClaimResponseDTO> pending = new ArrayList<>();
        pending.addAll(getClaimsByStatus(ClaimStatus.SUBMITTED));
        pending.addAll(getClaimsByStatus(ClaimStatus.UNDER_REVIEW));
        return pending;
    }

    private ClaimResponseDTO toResponseDTO(ClaimSubmission claim) {
        return ClaimResponseDTO.builder()
                .id(claim.getId())
                .reason(claim.getReason())
                .claimedAmount(claim.getClaimedAmount())
                .status(claim.getStatus())
                .filedAt(claim.getFiledAt())
                .policyId(claim.getPolicy().getId())
                .policyNumber(claim.getPolicy().getPolicyNumber())
                .claimantId(claim.getClaimant().getId())
                .claimantName(claim.getClaimant().getFullName())
                .adjudicatedById(claim.getAdjudicatedBy() != null ? claim.getAdjudicatedBy().getId() : null)
                .adjudicatedByName(claim.getAdjudicatedBy() != null ? claim.getAdjudicatedBy().getFullName() : null)
                .build();
    }
}