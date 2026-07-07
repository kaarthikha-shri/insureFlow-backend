package com.example.insureFlow.service;

import com.example.insureFlow.dto.request.PolicyCreateRequestDTO;
import com.example.insureFlow.dto.response.PolicyResponseDTO;
import com.example.insureFlow.entity.InsurancePolicy;
import com.example.insureFlow.entity.PolicyStatus;
import com.example.insureFlow.entity.SystemAccount;
import com.example.insureFlow.exception.BusinessValidationException;
import com.example.insureFlow.exception.ResourceNotFoundException;
import com.example.insureFlow.repository.InsurancePolicyRepository;
import com.example.insureFlow.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyUnderWritingService {

    private final InsurancePolicyRepository insurancePolicyRepository;
    private final SystemAccountRepository systemAccountRepository;

    
    @Transactional
    public PolicyResponseDTO createPolicy(PolicyCreateRequestDTO request, String underwriterEmail) {
        SystemAccount accountHolder = systemAccountRepository.findById(request.getAccountHolderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found with id: " + request.getAccountHolderId()));

        SystemAccount underwriter = systemAccountRepository.findByEmail(underwriterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Underwriter account not found."));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessValidationException("End date cannot be before start date.");
        }

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setCoverageType(request.getCoverageType());
        policy.setSumInsured(request.getSumInsured());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());
        policy.setStatus(PolicyStatus.PENDING);
        policy.setAccountHolder(accountHolder);
        policy.setUnderwriter(underwriter);

        InsurancePolicy saved = insurancePolicyRepository.save(policy);
        return toResponseDTO(saved);
    }

    
    @Transactional
    public PolicyResponseDTO activatePolicy(Long policyId) {
        InsurancePolicy policy = insurancePolicyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("No policy found with id: " + policyId));

        if (policy.getStatus() != PolicyStatus.PENDING) {
            throw new BusinessValidationException(
                    "Only PENDING policies can be activated. Current status: " + policy.getStatus());
        }

        if (policy.getRiskAssessments().isEmpty()) {
            throw new BusinessValidationException(
                    "Cannot activate a policy that has not been risk-assessed yet.");
        }

        policy.setStatus(PolicyStatus.ACTIVE);
        InsurancePolicy saved = insurancePolicyRepository.save(policy);
        return toResponseDTO(saved);
    }

    public PolicyResponseDTO getPolicyById(Long policyId) {
        InsurancePolicy policy = insurancePolicyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("No policy found with id: " + policyId));
        return toResponseDTO(policy);
    }

    // Used by the Policyholder's "/my-policies" endpoint when you already have the account id.
    public List<PolicyResponseDTO> getPoliciesForAccountHolder(Long accountHolderId) {
        return insurancePolicyRepository.findByAccountHolder_Id(accountHolderId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    
    public List<PolicyResponseDTO> getPoliciesForAccount(String email) {
        SystemAccount account = systemAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for email: " + email));
        return getPoliciesForAccountHolder(account.getId());
    }

    public List<PolicyResponseDTO> getPoliciesPendingAssessment() {
        return insurancePolicyRepository.findByStatusAndRiskAssessmentsIsEmpty(PolicyStatus.PENDING)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PolicyResponseDTO> getAllPolicies() {
        return insurancePolicyRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private String generatePolicyNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String suffix = String.valueOf(System.nanoTime()).substring(0, 4);
        return "POL-" + timestamp + "-" + suffix;
    }

    private PolicyResponseDTO toResponseDTO(InsurancePolicy policy) {
        return PolicyResponseDTO.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .coverageType(policy.getCoverageType())
                .sumInsured(policy.getSumInsured())
                .premiumAmount(policy.getPremiumAmount())
                .status(policy.getStatus())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .createdAt(policy.getCreatedAt())
                .accountHolderId(policy.getAccountHolder().getId())
                .accountHolderName(policy.getAccountHolder().getFullName())
                .underwriterId(policy.getUnderwriter() != null ? policy.getUnderwriter().getId() : null)
                .underwriterName(policy.getUnderwriter() != null ? policy.getUnderwriter().getFullName() : null)
                .build();
    }
}