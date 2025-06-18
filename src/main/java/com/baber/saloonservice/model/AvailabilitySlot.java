package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class AvailabilitySlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    // Many-to-one relationship with SaloonSpecialist
    @ManyToOne
    @JoinColumn(name = "saloon_specialist_id")
    private SaloonSpecialist saloonSpecialist;

    // Constructor with default values
    public AvailabilitySlot() {
        // Set default values
        this.date = LocalDate.now(); // Current date
        this.startTime = LocalTime.of(9, 0); // Default start time: 9:00 AM
        this.endTime = LocalTime.of(18, 0); // Default end time: 5:00 PM
    }

    // Additional constructor to allow setting custom values
    public AvailabilitySlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Method to generate future availability slots
    public static List<AvailabilitySlot> generateFutureSlots(int daysAhead, LocalTime startTime, LocalTime endTime) {
        List<AvailabilitySlot> futureSlots = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        // Generate availability slots for future dates
        for (int i = 0; i < daysAhead; i++) {
            LocalDate futureDate = currentDate.plusDays(i);
            AvailabilitySlot slot = new AvailabilitySlot(futureDate, startTime, endTime);
            futureSlots.add(slot);
        }

        return futureSlots;
    }
}
