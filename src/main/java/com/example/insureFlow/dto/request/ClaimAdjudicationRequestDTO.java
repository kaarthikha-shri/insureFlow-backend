package com.example.insureFlow.dto.request;

import com.example.insureFlow.entity.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClaimAdjudicationRequestDTO {

    @NotNull(message = "Decision is required")
    private ClaimStatus decision;

    private String adjudicationNotes;
}