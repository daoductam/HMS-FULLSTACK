import axiosInstance from "../Interceptor/AxiosInterceptor";

// --- Pharmacy Inventory Services ---

const addStock = async (data: any) => {
  const ENDPOINT = process.env.REACT_APP_ADD_STOCK_ENDPOINT;
  return axiosInstance
    .post(ENDPOINT!, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getStock = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_STOCK_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllStocks = async () => {
  const ENDPOINT = process.env.REACT_APP_GET_ALL_STOCKS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const updateStock = async (data: any) => {
  const ENDPOINT = process.env.REACT_APP_UPDATE_STOCK_ENDPOINT;
  return axiosInstance
    .put(ENDPOINT!, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// --- Exports ---
export { addStock, getStock, getAllStocks, updateStock };
