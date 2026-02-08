package com.hms.appointment.Appointment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctor_schedules")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(nullable = false)
    Long doctorId;
    
    @Column(nullable = false)
    LocalDate scheduleDate;
    
    @Column(nullable = false)
    Boolean isLocked; // true nếu bác sĩ nghỉ phép
    
    String lockReason; // Lý do khóa (nghỉ phép, bận việc, ...)
    
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<ScheduleShift> shifts = new ArrayList<>();
}


