import axiosInstance from "../Interceptor/AxiosInterceptor";

// --- Media Services ---

const uploadMedia = async (file: any) => {
  const ENDPOINT = process.env.REACT_APP_UPLOAD_MEDIA_ENDPOINT;
  const formData = new FormData();
  formData.append("file", file);

  return axiosInstance
    .post(ENDPOINT!, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getMedia = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_MEDIA_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// --- Exports ---
export { uploadMedia, getMedia };
