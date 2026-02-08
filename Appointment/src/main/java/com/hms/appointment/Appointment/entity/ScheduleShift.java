package com.hms.appointment.Appointment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "schedule_shifts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    DoctorSchedule schedule;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    Shift shift;
    
    @Column(nullable = false)
    Integer maxSlots; // Số slot tối đa trong ca này (ví dụ: 20)
    
    @Column(nullable = false)
    @Builder.Default
    Integer bookedSlots = 0; // Số slot đã đặt
}


