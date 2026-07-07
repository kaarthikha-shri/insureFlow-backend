package com.example.insureFlow.dto.response;

import com.example.insureFlow.entity.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponseDTO {

    private Long id;
    private String reason;
    private BigDecimal claimedAmount;
    private ClaimStatus status;
    private LocalDateTime filedAt;

    private Long policyId;
    private String policyNumber;
    private Long claimantId;
    private String claimantName;
    private Long adjudicatedById;
    private String adjudicatedByName;
}