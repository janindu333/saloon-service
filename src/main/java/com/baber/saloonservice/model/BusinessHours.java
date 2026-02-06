package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "business_hours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHours {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false)
    private Saloon salon;
    
    @Column(nullable = false, length = 20)
    private String dayOfWeek; // monday, tuesday, etc.
    
    @Column(nullable = false)
    private Boolean isOpen;
    
    @Column(length = 10)
    private String openTime; // HH:mm format
    
    @Column(length = 10)
    private String closeTime; // HH:mm format
    
    // Helper method to check if day is valid
    public static boolean isValidDay(String day) {
        String[] validDays = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        for (String validDay : validDays) {
            if (validDay.equalsIgnoreCase(day)) {
                return true;
            }
        }
        return false;
    }
}
