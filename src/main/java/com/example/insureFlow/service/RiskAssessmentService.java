package com.example.insureFlow.service;

import com.example.insureFlow.dto.request.RiskAssessmentRequestDTO;
import com.example.insureFlow.dto.response.RiskAssessmentResponseDTO;
import com.example.insureFlow.entity.InsurancePolicy;
import com.example.insureFlow.entity.RiskAssessment;
import com.example.insureFlow.entity.SystemAccount;
import com.example.insureFlow.exception.BusinessValidationException;
import com.example.insureFlow.exception.ResourceNotFoundException;
import com.example.insureFlow.repository.InsurancePolicyRepository;
import com.example.insureFlow.repository.RiskAssessmentRepository;
import com.example.insureFlow.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final SystemAccountRepository systemAccountRepository;

    @Transactional
    public RiskAssessmentResponseDTO createAssessment(RiskAssessmentRequestDTO request, String assessorEmail) {
        InsurancePolicy policy = insurancePolicyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No policy found with id: " + request.getPolicyId()));

        SystemAccount assessor = systemAccountRepository.findByEmail(assessorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Risk Assessor account not found."));

        if (policy.getStatus() != com.example.insureFlow.entity.PolicyStatus.PENDING) {
            throw new BusinessValidationException(
                    "Can only assess risk for PENDING policies. Current status: " + policy.getStatus());
        }

        RiskAssessment assessment = new RiskAssessment();
        assessment.setPolicy(policy);
        assessment.setAssessor(assessor);
        assessment.setRiskScore(request.getRiskScore());
        assessment.setRemarks(request.getRemarks());

        RiskAssessment saved = riskAssessmentRepository.save(assessment);
        return toResponseDTO(saved);
    }

    public List<RiskAssessmentResponseDTO> getAssessmentsForPolicy(Long policyId) {
        return riskAssessmentRepository.findByPolicy_Id(policyId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RiskAssessmentResponseDTO> getAssessmentsByAssessor(Long assessorId) {
        return riskAssessmentRepository.findByAssessor_Id(assessorId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private RiskAssessmentResponseDTO toResponseDTO(RiskAssessment assessment) {
        return RiskAssessmentResponseDTO.builder()
                .id(assessment.getId())
                .riskScore(assessment.getRiskScore())
                .remarks(assessment.getRemarks())
                .assessedAt(assessment.getAssessedAt())
                .policyId(assessment.getPolicy().getId())
                .policyNumber(assessment.getPolicy().getPolicyNumber())
                .assessorId(assessment.getAssessor().getId())
                .assessorName(assessment.getAssessor().getFullName())
                .build();
    }
}