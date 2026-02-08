package com.hms.appointment.Appointment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "shifts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(nullable = false, unique = true)
    String name; // "MORNING", "AFTERNOON"
    
    @Column(nullable = false)
    String displayName; // "Sáng", "Chiều"
    
    @Column(nullable = false)
    Integer startHour; // 7 cho Sáng, 13 cho Chiều
    
    @Column(nullable = false)
    Integer endHour; // 11 cho Sáng, 17 cho Chiều
}


