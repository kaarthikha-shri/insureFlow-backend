package com.example.insureFlow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskAssessmentResponseDTO {

    private Long id;
    private Integer riskScore;
    private String remarks;
    private LocalDateTime assessedAt;

    private Long policyId;
    private String policyNumber;
    private Long assessorId;
    private String assessorName;
}