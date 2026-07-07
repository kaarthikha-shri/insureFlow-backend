package com.example.insureFlow.service;

import com.example.insureFlow.entity.ClaimStatus;
import com.example.insureFlow.entity.PolicyStatus;
import com.example.insureFlow.repository.ClaimSubmissionRepository;
import com.example.insureFlow.repository.InsurancePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlatformAnalyticsService {

    private final InsurancePolicyRepository insurancePolicyRepository;
    private final ClaimSubmissionRepository claimSubmissionRepository;

    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        long activePolicies = insurancePolicyRepository.findByStatus(PolicyStatus.ACTIVE).size();
        long pendingPolicies = insurancePolicyRepository.findByStatus(PolicyStatus.PENDING).size();
        long submittedClaims = claimSubmissionRepository.findByStatus(ClaimStatus.SUBMITTED).size();
        long approvedClaims = claimSubmissionRepository.findByStatus(ClaimStatus.APPROVED).size();
        long disbursedClaims = claimSubmissionRepository.findByStatus(ClaimStatus.DISBURSED).size();

        BigDecimal totalPayout = claimSubmissionRepository.findByStatus(ClaimStatus.DISBURSED)
                .stream()
                .map(claim -> claim.getClaimedAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        summary.put("activePolicies", activePolicies);
        summary.put("pendingPolicies", pendingPolicies);
        summary.put("claimsAwaitingReview", submittedClaims);
        summary.put("approvedClaimsAwaitingDisbursement", approvedClaims);
        summary.put("disbursedClaims", disbursedClaims);
        summary.put("totalPayoutAmount", totalPayout);

        return summary;
    }
}