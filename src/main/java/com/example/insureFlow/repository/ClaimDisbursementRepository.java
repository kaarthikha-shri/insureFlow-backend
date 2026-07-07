package com.example.insureFlow.repository;

import com.example.insureFlow.entity.ClaimDisbursement;
import com.example.insureFlow.entity.DisbursementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimDisbursementRepository extends JpaRepository<ClaimDisbursement, Long> {

    
    Optional<ClaimDisbursement> findByClaim_Id(Long claimId);
    List<ClaimDisbursement> findByStatus(DisbursementStatus status);
}