import axiosInstance from "../Interceptor/AxiosInterceptor";

// --- Pharmacy Medicines Services ---

const addMedicine = async (data: any) => {
  const ENDPOINT = process.env.REACT_APP_ADD_MEDICINE_ENDPOINT;
  return axiosInstance
    .post(ENDPOINT!, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getMedicine = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_MEDICINE_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllMedicines = async () => {
  const ENDPOINT = process.env.REACT_APP_GET_ALL_MEDICINES_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const updateMedicine = async (data: any) => {
  const ENDPOINT = process.env.REACT_APP_UPDATE_MEDICINE_ENDPOINT;
  return axiosInstance
    .put(ENDPOINT!, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// --- Exports ---
export { addMedicine, getMedicine, getAllMedicines, updateMedicine };
