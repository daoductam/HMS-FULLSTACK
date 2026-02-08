import { useEffect, useState } from "react";
import axiosInstance from "../../../Interceptor/AxiosInterceptor";

const useProtectedImage = (imageId?: string | null) => {
  const [imageUrl, setImageUrl] = useState<string>("/image.png");
  useEffect(() => {
    if (!imageId) return;
    axiosInstance
      .get("/media/" + imageId, { responseType: "blob" })
      .then((response) => {
        const url = URL.createObjectURL(response.data);
        setImageUrl(url);
      })
      .catch((err) => {
        console.error("Lỗi fetching protecred image", err);
      });
  }, [imageId]);
  return imageUrl;
};

export default useProtectedImage;
