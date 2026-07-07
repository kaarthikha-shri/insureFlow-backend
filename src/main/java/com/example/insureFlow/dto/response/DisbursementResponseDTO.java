package com.example.insureFlow.dto.response;

import com.example.insureFlow.entity.DisbursementStatus;
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
public class DisbursementResponseDTO {

    private Long id;
    private BigDecimal disbursedAmount;
    private DisbursementStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;

    private Long claimId;
}