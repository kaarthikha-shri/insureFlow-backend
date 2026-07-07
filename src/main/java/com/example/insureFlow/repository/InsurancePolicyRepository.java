package com.example.insureFlow.repository;

import com.example.insureFlow.entity.InsurancePolicy;
import com.example.insureFlow.entity.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {

    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);
    List<InsurancePolicy> findByAccountHolder_Id(Long accountHolderId);
    List<InsurancePolicy> findByUnderwriter_Id(Long underwriterId);
    List<InsurancePolicy> findByStatus(PolicyStatus status);
    @Query("SELECT p FROM InsurancePolicy p WHERE p.status = :status AND p.riskAssessments IS EMPTY")
    List<InsurancePolicy> findPendingPoliciesWithoutAssessment(@Param("status") PolicyStatus status);
     List<InsurancePolicy> findByStatusAndRiskAssessmentsIsEmpty(PolicyStatus status);
}