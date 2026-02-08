import axiosInstance from "../Interceptor/AxiosInterceptor";

// --- User Services ---

const registerUser = async (user: any) => {
  const ENDPOINT = process.env.REACT_APP_REGISTER_USER_ENDPOINT;
  return axiosInstance
    .post(ENDPOINT!, user)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const loginUser = async (user: any) => {
  const ENDPOINT = process.env.REACT_APP_LOGIN_USER_ENDPOINT;
  return axiosInstance
    .post(ENDPOINT!, user)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getUserProfile = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_USER_PROFILE_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getRegistrationCounts = async () => {
  const ENDPOINT = process.env.REACT_APP_GET_REGISTRATION_COUNTS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// Lấy danh sách bác sĩ đang chờ duyệt (Pending)
export const getPendingDoctors = async () => {
  // API endpoint này phải khớp với Backend bạn đã viết
  const response = await axiosInstance.get("/user/getPendingDoctors");
  return response.data;
};

// Duyệt bác sĩ (Approve)
export const approveDoctor = async (id: number) => {
  const response = await axiosInstance.put(`/user/admin/approve/${id}`);
  return response.data;
};

// Từ chối bác sĩ (Reject)
export const rejectDoctor = async (id: number) => {
  const response = await axiosInstance.put(`/user/admin/reject/${id}`);
  return response.data;
};

// --- Exports ---
export { registerUser, loginUser, getUserProfile, getRegistrationCounts };
