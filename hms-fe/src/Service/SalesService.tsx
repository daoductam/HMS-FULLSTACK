import axiosInstance from "../Interceptor/AxiosInterceptor";

// --- Pharmacy Sales Services ---

const addSale = async (data: any) => {
  const ENDPOINT = process.env.REACT_APP_ADD_SALE_ENDPOINT;
  return axiosInstance
    .post(ENDPOINT!, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getSale = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_SALE_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllSaleItems = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_ALL_SALE_ITEMS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const updateSale = async (data: any) => {
  const ENDPOINT = process.env.REACT_APP_UPDATE_SALE_ENDPOINT;
  return axiosInstance
    .put(ENDPOINT!, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllSales = async () => {
  const ENDPOINT = process.env.REACT_APP_GET_ALL_SALES_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// --- Exports ---
export { addSale, getSale, getAllSaleItems, updateSale, getAllSales };
