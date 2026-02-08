package com.hms.hms_common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEvent {
    private Long appointmentId;
    private String patientEmail;
    private String patientName;
    private String doctorName;
    // Lưu ý: LocalDateTime cần cấu hình Jackson JavaTimeModule ở Service
    // Nếu muốn an toàn tuyệt đối khi serialize, có thể dùng String
    private String appointmentTime;
    private String status;
}