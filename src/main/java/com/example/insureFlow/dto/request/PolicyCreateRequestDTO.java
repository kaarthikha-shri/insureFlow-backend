package com.example.insureFlow.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PolicyCreateRequestDTO {

    @NotNull(message = "Account holder (policyholder) ID is required")
    private Long accountHolderId;

    @NotBlank(message = "Coverage type is required")
    private String coverageType;

    @NotNull
    @Positive(message = "Sum insured must be positive")
    private BigDecimal sumInsured;

    @NotNull
    @Positive(message = "Premium amount must be positive")
    private BigDecimal premiumAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}