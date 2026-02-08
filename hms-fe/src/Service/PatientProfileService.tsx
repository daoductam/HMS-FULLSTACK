import axiosInstance from "../Interceptor/AxiosInterceptor";

// --- Patient Services ---

const getPatient = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_PATIENT_PROFILE_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const updatePatient = async (patient: any) => {
  const ENDPOINT = process.env.REACT_APP_UPDATE_PATIENT_PROFILE_ENDPOINT;
  return axiosInstance
    .put(ENDPOINT!, patient)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllPatients = async () => {
  const ENDPOINT = process.env.REACT_APP_GET_ALL_PATIENTS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllPatientsPaginated = async (page: number = 0, size: number = 10) => {
  const ENDPOINT = process.env.REACT_APP_GET_ALL_PATIENTS_ENDPOINT?.replace('/getAll', '/getAllPaginated') || '/profile/patient/getAllPaginated';
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
export { getPatient, updatePatient, getAllPatients, getAllPatientsPaginated };
