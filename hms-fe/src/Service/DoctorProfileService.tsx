import axiosInstance from "../Interceptor/AxiosInterceptor";

// --- Doctor Services ---

const getDoctor = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_DOCTOR_PROFILE_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const updateDoctor = async (doctor: any) => {
  const ENDPOINT = process.env.REACT_APP_UPDATE_DOCTOR_PROFILE_ENDPOINT;
  return axiosInstance
    .put(ENDPOINT!, doctor)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getDoctorDropdowns = async () => {
  const ENDPOINT = process.env.REACT_APP_GET_DOCTOR_DROPDOWNS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllDoctors = async () => {
  const ENDPOINT = process.env.REACT_APP_GET_ALL_DOCTORS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllDoctorsPaginated = async (page: number = 0, size: number = 10) => {
  const ENDPOINT = process.env.REACT_APP_GET_ALL_DOCTORS_ENDPOINT?.replace('/getAll', '/getAllPaginated') || '/profile/doctor/getAllPaginated';
  return axiosInstance
    .get(ENDPOINT!, {
      params: { page, size }
    })
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// --- Exports ---
export { getDoctor, updateDoctor, getDoctorDropdowns, getAllDoctors, getAllDoctorsPaginated };
