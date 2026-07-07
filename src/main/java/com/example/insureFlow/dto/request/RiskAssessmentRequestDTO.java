package com.example.insureFlow.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RiskAssessmentRequestDTO {

    @NotNull(message = "Policy ID is required")
    private Long policyId;

    @NotNull
    @Min(value = 0, message = "Risk score cannot be below 0")
    @Max(value = 100, message = "Risk score cannot exceed 100")
    private Integer riskScore;

    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    private String remarks;
}