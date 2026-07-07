package com.example.insureFlow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Claim reason is required")
    @Column(nullable = false, length = 1000)
    private String reason;

    @NotNull
    @Positive(message = "Claimed amount must be positive")
    @Column(nullable = false)
    private BigDecimal claimedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.SUBMITTED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime filedAt;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy policy;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimant_id", nullable = false)
    private SystemAccount claimant;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjudicated_by")
    private SystemAccount adjudicatedBy;

    
    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL)
    private ClaimDisbursement disbursement;

    @PrePersist
    protected void onCreate() {
        this.filedAt = LocalDateTime.now();
    }
}