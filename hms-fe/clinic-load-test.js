// import http from "k6/http";
// import { check, sleep } from "k6";
// import { randomIntBetween } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";
// import exec from "k6/execution"; // Thư viện giúp lấy thông tin người dùng ảo (VU)

// // --- 1. CẤU HÌNH KỊCH BẢN (Scenario) ---
// export const options = {
//   stages: [
//     { duration: "10s", target: 5 }, // Giai đoạn 1: Làm nóng máy (5 người)
//     { duration: "20s", target: 20 }, // Giai đoạn 2: Tải trung bình (20 người)
//     { duration: "10s", target: 0 }, // Giai đoạn 3: Giảm dần về 0
//   ],
//   thresholds: {
//     http_req_duration: ["p(95)<2000"], // Mong muốn 95% request xong dưới 2s
//     http_req_failed: ["rate<0.01"], // Tỷ lệ lỗi không quá 1%
//   },
// };

// const BASE_URL = "http://localhost:9000";

// export default function () {
//   // Lấy ID của người dùng ảo hiện tại (VD: User 1, User 2...)
//   const vuId = exec.vu.idInTest;

//   // LOGIC HIỂN THỊ: Chỉ hiện thông báo cho User số 1, 5, 10 để demo cho gọn màn hình
//   // Hoặc nếu có lỗi thì LUÔN LUÔN hiện
//   const isDemoUser = vuId === 1 || vuId === 5 || vuId === 10;

//   // --- BƯỚC 1: ĐĂNG NHẬP ---
//   if (isDemoUser) console.log(`👤 [User ${vuId}] Đang đăng nhập hệ thống...`);

//   const loginPayload = JSON.stringify({
//     email: "tamdao1742005@gmail.com",
//     password: "Tam123456@",
//   });

//   const loginParams = { headers: { "Content-Type": "application/json" } };

//   const resLogin = http.post(
//     `${BASE_URL}/user/login`,
//     loginPayload,
//     loginParams
//   );

//   // Kiểm tra kết quả (Check) - Đặt tên tiếng Việt cho dễ hiểu trên bảng tổng kết
//   const loginSuccess = check(resLogin, {
//     "✅ B1. Đăng nhập thành công (200 OK)": (r) => r.status === 200,
//     "🔑 B1. Token hợp lệ": (r) => r.body && r.body.length > 10,
//   });

//   if (!loginSuccess) {
//     // Nếu lỗi thì in màu đỏ (error) để mọi người chú ý ngay
//     console.error(
//       `❌ [User ${vuId}] Đăng nhập LỖI! Status: ${resLogin.status} - Body: ${resLogin.body}`
//     );
//     return; // Dừng luồng này ngay
//   }

//   // Lấy token (API trả về raw string)
//   const token = resLogin.body;

//   // --- BƯỚC 2: ĐẶT LỊCH KHÁM ---
//   sleep(1); // Giả lập người dùng đang suy nghĩ chọn ngày (1 giây)

//   const randomDay = randomIntBetween(1, 28);
//   const appointmentTime = `2025-12-${
//     randomDay < 10 ? "0" + randomDay : randomDay
//   }T09:00:00`;

//   if (isDemoUser)
//     console.log(
//       `📅 [User ${vuId}] Đang chọn lịch khám ngày: ${appointmentTime}`
//     );

//   const appointmentPayload = JSON.stringify({
//     patientId: 1,
//     doctorId: 2,
//     appointmentTime: appointmentTime,
//     reason: "Kiểm tra hiệu năng (Demo)",
//     status: "SCHEDULED",
//   });

//   const authParams = {
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: `Bearer ${token}`,
//     },
//   };

//   const resBook = http.post(
//     `${BASE_URL}/appointment/schedule`,
//     appointmentPayload,
//     authParams
//   );

//   const isBookSuccess = check(resBook, {
//     "✅ B2. Đặt lịch thành công (200/201)": (r) =>
//       r.status === 200 || r.status === 201,
//   });

//   if (isBookSuccess) {
//     if (isDemoUser) console.log(`🎉 [User ${vuId}] Đã đặt lịch THÀNH CÔNG!`);
//   } else {
//     // In chi tiết lỗi để cả team cùng debug
//     console.error(
//       `🔥 [User ${vuId}] Đặt lịch THẤT BẠI! Status: ${resBook.status}`
//     );
//     console.error(`   👉 Chi tiết lỗi Server: ${resBook.body}`);
//   }

//   // Nghỉ ngẫu nhiên 1-3s trước khi user này thực hiện vòng lặp tiếp theo
//   sleep(randomIntBetween(1, 3));
// }

// import http from "k6/http";
// import { check, sleep } from "k6";
// import { randomIntBetween } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";
// import exec from "k6/execution";

// // --- CẤU HÌNH TẢI (MAX 100 USERS) ---
// export const options = {
//   stages: [
//     { duration: "10s", target: 20 },  // Giai đoạn 1: Khởi động nhẹ (20 người)
//     { duration: "20s", target: 50 },  // Giai đoạn 2: Tăng tốc (50 người)
//     { duration: "20s", target: 100 }, // Giai đoạn 3: Đạt đỉnh (100 người)
//     { duration: "40s", target: 100 }, // Giai đoạn 4: Duy trì đỉnh tải trong 40s (Stress)
//     { duration: "10s", target: 0 },   // Giai đoạn 5: Hạ nhiệt
//   ],
//   thresholds: {
//     http_req_duration: ["p(95)<1500"], // Mong đợi 95% request xong dưới 1.5s
//     http_req_failed: ["rate<0.01"],    // Tỷ lệ lỗi cho phép dưới 1%
//   },
// };

// const BASE_URL = "http://localhost:9000";

// export default function () {
//   const vuId = exec.vu.idInTest;

//   // Chỉ hiện log cho User đầu, giữa và cuối để dễ theo dõi
//   const isDemoUser = (vuId === 1 || vuId === 50 || vuId === 100);

//   // --- BƯỚC 1: ĐĂNG NHẬP ---
//   if (isDemoUser) console.log(`👤 [User ${vuId}] Đang đăng nhập...`);

//   const loginPayload = JSON.stringify({
//     email: "tamdao1742005@gmail.com",
//     password: "Tam123456@",
//   });

//   const loginParams = { headers: { "Content-Type": "application/json" } };

//   const resLogin = http.post(`${BASE_URL}/user/login`, loginPayload, loginParams);

//   const loginSuccess = check(resLogin, {
//     "Login status 200": (r) => r.status === 200,
//     "Has Token": (r) => r.body && r.body.length > 10,
//   });

//   if (!loginSuccess) {
//     if (isDemoUser) console.error(`❌ [User ${vuId}] Login Fail: ${resLogin.status}`);
//     return;
//   }

//   const token = resLogin.body;

//   // --- BƯỚC 2: ĐẶT LỊCH ---
//   sleep(randomIntBetween(1, 3)); // Nghỉ ngẫu nhiên 1-3s

//   const randomDay = randomIntBetween(1, 28);
//   const appointmentTime = `2025-12-${randomDay < 10 ? "0" + randomDay : randomDay}T09:00:00`;

//   const appointmentPayload = JSON.stringify({
//     patientId: 1,
//     doctorId: 2,
//     appointmentTime: appointmentTime,
//     reason: "Load Test 100 VUs",
//     status: "SCHEDULED",
//   });

//   const authParams = {
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: `Bearer ${token}`,
//     },
//   };

//   const resBook = http.post(`${BASE_URL}/appointment/schedule`, appointmentPayload, authParams);

//   const isBookSuccess = check(resBook, {
//     "Booking status 200/201": (r) => r.status === 200 || r.status === 201,
//   });

//   if (isBookSuccess) {
//     if (isDemoUser) console.log(`🎉 [User ${vuId}] Đặt lịch OK!`);
//   } else {
//     // In lỗi đỏ nếu thất bại
//     console.error(`🔥 [User ${vuId}] Booking Fail: ${resBook.status}`);
//   }
// }

import http from "k6/http";
import { check, sleep } from "k6";
import { randomIntBetween } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";
import exec from "k6/execution";

// --- CẤU HÌNH STRESS TEST (1000 USERS) ---
export const options = {
  stages: [
    { duration: "30s", target: 200 }, // Giai đoạn 1: Khởi động lên 200 users
    { duration: "1m", target: 500 }, // Giai đoạn 2: Tăng tốc lên 500 users
    { duration: "1m", target: 1000 }, // Giai đoạn 3: Đạt đỉnh 1000 users
    { duration: "40s", target: 1000 }, // Giai đoạn 4: Duy trì đỉnh tải trong 40s (Stress cực đại)
    { duration: "30s", target: 0 }, // Giai đoạn 5: Hạ nhiệt về 0
  ],
  thresholds: {
    http_req_duration: ["p(95)<3000"], // Chấp nhận chờ tới 3s khi quá tải
    http_req_failed: ["rate<0.05"], // Chấp nhận lỗi dưới 5% (hệ thống có thể từ chối phục vụ)
  },
};

const BASE_URL = "http://localhost:9000";

export default function () {
  const vuId = exec.vu.idInTest;

  // Chỉ hiện log cho User đại diện: Đầu, Giữa và Cuối
  const isDemoUser = vuId === 1 || vuId === 500 || vuId === 1000;

  // --- BƯỚC 1: ĐĂNG NHẬP ---
  if (isDemoUser) console.log(`👤 [User ${vuId}] Đang đăng nhập...`);

  const loginPayload = JSON.stringify({
    email: "tamdao1742005@gmail.com",
    password: "Tam123456@",
  });

  const loginParams = { headers: { "Content-Type": "application/json" } };

  const resLogin = http.post(
    `${BASE_URL}/user/login`,
    loginPayload,
    loginParams
  );

  const loginSuccess = check(resLogin, {
    "Login status 200": (r) => r.status === 200,
    "Has Token": (r) => r.body && r.body.length > 10,
  });

  if (!loginSuccess) {
    // Chỉ in lỗi nếu là user demo để tránh spam hàng nghìn dòng lỗi
    if (isDemoUser)
      console.error(`❌ [User ${vuId}] Login Fail: ${resLogin.status}`);
    return;
  }

  const token = resLogin.body;

  // --- BƯỚC 2: ĐẶT LỊCH ---
  // Tăng thời gian nghỉ ngẫu nhiên lên 2-5s để giảm bớt áp lực dồn dập cho máy tính cá nhân
  sleep(randomIntBetween(2, 5));

  const randomDay = randomIntBetween(1, 28);
  const appointmentTime = `2025-12-${
    randomDay < 10 ? "0" + randomDay : randomDay
  }T09:00:00`;

  const appointmentPayload = JSON.stringify({
    patientId: 1,
    doctorId: 2,
    appointmentTime: appointmentTime,
    reason: "Stress Test 1000 VUs",
    status: "SCHEDULED",
  });

  const authParams = {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  };

  const resBook = http.post(
    `${BASE_URL}/appointment/schedule`,
    appointmentPayload,
    authParams
  );

  const isBookSuccess = check(resBook, {
    "Booking status 200/201": (r) => r.status === 200 || r.status === 201,
  });

  if (isBookSuccess) {
    if (isDemoUser) console.log(`🎉 [User ${vuId}] Đặt lịch OK!`);
  } else {
    // In lỗi đỏ nếu thất bại (Chỉ in cho demo user hoặc xác suất nhỏ 0.1% để debug)
    if (isDemoUser || Math.random() < 0.001) {
      console.error(`🔥 [User ${vuId}] Booking Fail: ${resBook.status}`);
    }
  }
}
