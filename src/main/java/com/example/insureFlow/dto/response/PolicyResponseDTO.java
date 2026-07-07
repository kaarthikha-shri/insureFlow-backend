package com.example.insureFlow.dto.response;

import com.example.insureFlow.entity.PolicyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyResponseDTO {

    private Long id;
    private String policyNumber;
    private String coverageType;
    private BigDecimal sumInsured;
    private BigDecimal premiumAmount;
    private PolicyStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    
    private Long accountHolderId;
    private String accountHolderName;
    private Long underwriterId;
    private String underwriterName;
}