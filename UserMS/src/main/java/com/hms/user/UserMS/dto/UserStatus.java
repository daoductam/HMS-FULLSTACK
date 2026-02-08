package com.hms.user.UserMS.dto;

public enum UserStatus {
    PENDING,    // Chờ duyệt (Dành cho bác sĩ mới đăng ký)
    ACTIVE,     // Đang hoạt động (Bệnh nhân hoặc Bác sĩ đã duyệt)
    LOCKED,     // Bị khóa
    REJECTED    // Từ chối duyệt
}
