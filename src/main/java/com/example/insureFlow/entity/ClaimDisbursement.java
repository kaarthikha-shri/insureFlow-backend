package com.example.insureFlow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_disbursements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDisbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive(message = "Disbursed amount must be positive")
    @Column(nullable = false)
    private BigDecimal disbursedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisbursementStatus status = DisbursementStatus.SCHEDULED;

    private LocalDateTime scheduledAt;

    private LocalDateTime completedAt;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false, unique = true)
    private ClaimSubmission claim;

    @PrePersist
    protected void onCreate() {
        this.scheduledAt = LocalDateTime.now();
    }
}