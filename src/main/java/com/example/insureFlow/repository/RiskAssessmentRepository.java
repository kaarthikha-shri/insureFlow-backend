package com.example.insureFlow.repository;

import com.example.insureFlow.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    List<RiskAssessment> findByPolicy_Id(Long policyId);
    List<RiskAssessment> findByAssessor_Id(Long assessorId);
}