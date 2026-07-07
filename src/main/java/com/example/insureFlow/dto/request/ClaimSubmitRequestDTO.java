package com.example.insureFlow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimSubmitRequestDTO {

    @NotNull(message = "Policy ID is required")
    private Long policyId;

    @NotBlank(message = "Reason for the claim is required")
    private String reason;

    @NotNull
    @Positive(message = "Claimed amount must be positive")
    private BigDecimal claimedAmount;
}