package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false, unique = true)
    private Saloon salon;
    
    // Accepted payment methods
    @Column(nullable = false)
    private Boolean cashAccepted = true;
    
    @Column(nullable = false)
    private Boolean cardAccepted = true;
    
    @Column(nullable = false)
    private Boolean onlineAccepted = false;
    
    @Column(nullable = false)
    private Boolean mobileAccepted = false;
    
    // Deposit settings
    @Column(nullable = false)
    private Boolean depositRequired = false;
    
    @Column(length = 20)
    private String depositType; // fixed, percentage
    
    @Column(precision = 10, scale = 2)
    private BigDecimal depositAmount;
    
    // Cancellation settings
    @Column
    private Integer cancellationAllowedUntil; // hours before appointment (null = anytime, 0 = not allowed)
    
    @Column(length = 20)
    private String cancellationFeeType; // none, deposit, percentage, full
    
    @Column(precision = 5, scale = 2)
    private BigDecimal cancellationFeePercentage;
    
    // No-show penalty
    @Column(length = 20)
    private String noShowPenalty; // none, full, percentage, block
}
