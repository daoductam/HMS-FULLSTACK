import React, { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { Button, Card, Loader, Text, Title } from "@mantine/core";
import { IconCheck, IconX } from "@tabler/icons-react";
import { successNotification, errorNotification } from "../../Utility/NotificationUtil";
import { getSale } from "../../Service/SalesService";

const PaymentResultPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [saleId, setSaleId] = useState<string | null>(null);
  const [paymentStatus, setPaymentStatus] = useState<"checking" | "success" | "failed">("checking");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Lấy orderId từ MoMo redirect (format: SALE-{saleId})
    const orderId = searchParams.get("orderId");
    const resultCode = searchParams.get("resultCode");

    if (orderId && orderId.startsWith("SALE-")) {
      const id = orderId.replace("SALE-", "");
      setSaleId(id);
      
      // Kiểm tra payment status
      checkPaymentStatus(id);
    } else {
      errorNotification("Không tìm thấy thông tin đơn hàng");
      setTimeout(() => {
        navigate("/admin/sales");
      }, 2000);
    }
  }, [searchParams, navigate]);

  const checkPaymentStatus = async (id: string) => {
    try {
      const sale = await getSale(id);
      if (sale.status === "PAID") {
        setPaymentStatus("success");
        successNotification("Thanh toán thành công!");
        // Redirect về sales page sau 2 giây
        setTimeout(() => {
          window.location.href = "/admin/sales";
        }, 2000);
      } else {
        setPaymentStatus("failed");
        errorNotification("Thanh toán thất bại hoặc chưa hoàn tất");
      }
    } catch (error) {
      console.error("Error checking payment status:", error);
      setPaymentStatus("failed");
      errorNotification("Không thể kiểm tra trạng thái thanh toán");
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    window.location.href = "/admin/sales";
  };

  if (loading || paymentStatus === "checking") {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <Loader size="lg" />
          <Text mt="md" size="sm" color="dimmed">
            Đang kiểm tra trạng thái thanh toán...
          </Text>
        </div>
      </div>
    );
  }

  if (paymentStatus === "success") {
    return (
      <div className="flex items-center justify-center min-h-screen p-5">
        <Card shadow="md" padding="xl" radius="md" withBorder className="max-w-md w-full">
          <div className="flex flex-col items-center text-center">
            <div className="bg-green-100 rounded-full p-4 mb-4">
              <IconCheck size={48} className="text-green-600" />
            </div>
            <Title order={2} mb="md" className="text-green-600">
              Thanh toán thành công!
            </Title>
            <Text size="sm" color="dimmed" mb="xl">
              Đơn hàng của bạn đã được thanh toán thành công.
            </Text>
            <Button onClick={handleBack} fullWidth>
              Quay lại trang bán hàng
            </Button>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center min-h-screen p-5">
      <Card shadow="md" padding="xl" radius="md" withBorder className="max-w-md w-full">
        <div className="flex flex-col items-center text-center">
          <div className="bg-red-100 rounded-full p-4 mb-4">
            <IconX size={48} className="text-red-600" />
          </div>
          <Title order={2} mb="md" className="text-red-600">
            Thanh toán thất bại
          </Title>
          <Text size="sm" color="dimmed" mb="xl">
            Vui lòng thử lại hoặc liên hệ hỗ trợ.
          </Text>
          <Button onClick={handleBack} fullWidth variant="outline">
            Quay lại trang bán hàng
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default PaymentResultPage;


