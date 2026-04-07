package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "staff_invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffInvitation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID invitationToken;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false)
    private Saloon salon;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false, length = 20)
    private String role; // staff, manager, receptionist
    
    @Column(nullable = false, length = 20)
    private String status; // pending, accepted, rejected, cancelled
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime expiresAt;
    
    @Column
    private LocalDateTime acceptedAt;
    
    @PrePersist
    protected void onCreate() {
        if (invitationToken == null) {
            invitationToken = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "pending";
        }
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusDays(7); // 7 days expiration
        }
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return "pending".equals(status) && !isExpired();
    }
}
