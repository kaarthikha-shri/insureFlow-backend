package com.example.insureFlow.repository;

import com.example.insureFlow.entity.ClaimStatus;
import com.example.insureFlow.entity.ClaimSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ClaimSubmissionRepository extends JpaRepository<ClaimSubmission, Long> {
    List<ClaimSubmission> findByClaimant_Id(Long claimantId);
    List<ClaimSubmission> findByPolicy_Id(Long policyId);
    List<ClaimSubmission> findByStatus(ClaimStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ClaimSubmission c WHERE c.id = :id")
    Optional<ClaimSubmission> findByIdForUpdate(@Param("id") Long id);
}