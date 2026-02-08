import axios, { InternalAxiosRequestConfig } from "axios";
const axiosInstance = axios.create({
  baseURL:
    process.env.REACT_APP_API_URL || process.env.REACT_APP_LOCAL_BACKEND_URL,
});
// Lấy Base URL từ biến môi trường
const BASE_URL = process.env.REACT_APP_LOCAL_BACKEND_URL;

// Đảm bảo giá trị BASE_URL KHÔNG bị thiếu
console.log("Kiểm tra BASE_URL:", BASE_URL);
axiosInstance.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem("token");
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});



export default axiosInstance;
