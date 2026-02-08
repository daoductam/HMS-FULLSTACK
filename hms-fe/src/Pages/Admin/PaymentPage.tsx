import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Button, Card, Loader, Text, Title } from "@mantine/core";
import { IconCheck, IconX } from "@tabler/icons-react";
import { successNotification, errorNotification } from "../../Utility/NotificationUtil";
import { getSale } from "../../Service/SalesService";

const PaymentPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [paymentUrl, setPaymentUrl] = useState<string | null>(null);
  const [saleId, setSaleId] = useState<string | null>(null);
  const [paymentStatus, setPaymentStatus] = useState<"pending" | "success" | "failed">("pending");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Kiểm tra xem có phải redirect từ MoMo không (có orderId trong query params)
    const orderId = searchParams.get("orderId");
    const resultCode = searchParams.get("resultCode");
    const partnerCode = searchParams.get("partnerCode");
    
    // Nếu có orderId từ MoMo redirect, chuyển sang payment-result page
    if (orderId && (orderId.startsWith("SALE-") || partnerCode)) {
      const saleIdFromOrder = orderId.replace("SALE-", "");
      // Redirect đến payment-result page với orderId
      navigate(`/admin/payment-result?orderId=${orderId}`, { replace: true });
      return;
    }

    // Nếu không phải redirect từ MoMo, lấy paymentUrl và saleId từ query params để hiển thị iframe
    const url = searchParams.get("paymentUrl");
    const id = searchParams.get("saleId");

    // Nếu có paymentUrl nhưng không có saleId, có thể là redirect từ MoMo sau khi thanh toán
    // Trong trường hợp này, redirect về sales page và để backend xử lý qua IPN callback
    if (url && !id) {
      // Có thể là redirect từ MoMo, chờ một chút để backend xử lý IPN callback
      setTimeout(() => {
        window.location.href = "/admin/sales";
      }, 2000);
      return;
    }

    if (url && id) {
      setPaymentUrl(url);
      setSaleId(id);
      setLoading(false);
    } else {
      errorNotification("Thiếu thông tin thanh toán");
      navigate("/admin/sales");
    }
  }, [searchParams, navigate]);

  // Kiểm tra payment status khi iframe load
  const handleIframeLoad = () => {
    // Kiểm tra URL hiện tại của iframe để xác định payment status
    // MoMo sẽ redirect về redirectUrl sau khi thanh toán
    const iframe = document.getElementById("momo-payment-iframe") as HTMLIFrameElement;
    if (iframe) {
      try {
        const iframeUrl = iframe.contentWindow?.location.href;
        if (iframeUrl && iframeUrl.includes("payment-result")) {
          // Kiểm tra payment status từ backend
          checkPaymentStatus();
        }
      } catch (e) {
        // Cross-origin error, không thể đọc iframe URL
        // Sẽ dùng polling hoặc message event
      }
    }
  };

  // Polling để kiểm tra payment status
  useEffect(() => {
    if (!saleId || paymentStatus !== "pending") return;

    const interval = setInterval(() => {
      checkPaymentStatus();
    }, 3000); // Check mỗi 3 giây

    return () => clearInterval(interval);
  }, [saleId, paymentStatus]);

  const checkPaymentStatus = async () => {
    if (!saleId) return;

    try {
      const sale = await getSale(saleId);
      if (sale.status === "PAID") {
        setPaymentStatus("success");
        successNotification("Thanh toán thành công!");
        // Redirect về sales page sau 2 giây
        setTimeout(() => {
          window.location.href = "/admin/sales";
        }, 2000);
      } else if (sale.status === "FAILED" || sale.status === "CANCELLED") {
        setPaymentStatus("failed");
        errorNotification("Thanh toán thất bại hoặc đã hủy");
      }
      // Nếu status vẫn là PENDING, tiếp tục polling
    } catch (error) {
      console.error("Error checking payment status:", error);
    }
  };

  const handleBack = () => {
    navigate("/admin/sales");
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader size="lg" />
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

  if (paymentStatus === "failed") {
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
  }

  return (
    <div className="p-5">
      <div className="mb-4 flex items-center justify-between">
        <Title order={3}>Thanh toán MoMo</Title>
        <Button variant="subtle" onClick={handleBack}>
          Hủy
        </Button>
      </div>
      <Card shadow="md" padding={0} radius="md" withBorder>
        <div style={{ position: "relative", paddingBottom: "100vh", minHeight: "600px", overflow: "hidden" }}>
          <iframe
            id="momo-payment-iframe"
            src={paymentUrl || ""}
            style={{
              position: "absolute",
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
              border: "none",
              minHeight: "600px",
            }}
            title="MoMo Payment"
            onLoad={handleIframeLoad}
            allow="payment; fullscreen"
          />
        </div>
      </Card>
      <Text size="sm" color="dimmed" mt="md" className="text-center">
        Vui lòng hoàn tất thanh toán trong cửa sổ trên. Trang sẽ tự động cập nhật khi thanh toán thành công.
      </Text>
    </div>
  );
};

export default PaymentPage;

