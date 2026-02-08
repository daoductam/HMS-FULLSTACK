import React, { useState, useRef, useEffect } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../Store";

// ==========================================
// 1. CẤU HÌNH & DỮ LIỆU CỐ ĐỊNH (LOCAL LOGIC)
// ==========================================
const CONFIG = {
  clinicName: "Phòng khám Thông Minh",
  welcomeMessage: "Xin chào! Tôi là trợ lý AI. Tôi có thể giúp gì cho bạn?",
  themeColor: "bg-blue-600",
  apiEndpoint: "http://localhost:8000/chat", // Địa chỉ Python Backend
};

// Dữ liệu trả lời nhanh (không cần AI)
const STATIC_DATA = {
  working_hours: "⏰ Giờ làm việc: Thứ 2-6: 8:00-17:00, Thứ 7: 8:00-12:00",
  address: "📍 Địa chỉ: 298 Đường Cầu Diễn, Nhổn, Minh Khai, Từ Liêm, Hà Nội",
  insurance: "🛡️ BHYT: Chấp nhận BHYT và bảo hiểm tư nhân (Bảo Việt, PVI...).",
  cost: "💰 Chi phí: Khám tổng quát 200k. Dịch vụ khác vui lòng liên hệ lễ tân.",
  contact: "📞 Hotline cấp cứu: 1900 1234",
};

const QUICK_REPLIES = [
  { text: "⏰ Giờ làm việc", action: "working_hours" },
  { text: "📍 Địa chỉ", action: "address" },
  { text: "🛡️ Bảo hiểm", action: "insurance" },
  { text: "💰 Chi phí", action: "cost" },
];

const KEYWORD_RULES = [
  { keywords: ["giờ", "mấy giờ", "làm việc", "mở cửa"], key: "working_hours" },
  { keywords: ["địa chỉ", "ở đâu", "chỗ nào", "vị trí"], key: "address" },
  { keywords: ["bảo hiểm", "bhyt", "thanh toán"], key: "insurance" },
  { keywords: ["giá", "tiền", "chi phí", "bao nhiêu"], key: "cost" },
  { keywords: ["hotline", "sdt", "điện thoại"], key: "contact" },
];

// ==========================================
// 2. COMPONENT CHÍNH
// ==========================================
interface Message {
  id: number;
  sender: "user" | "bot";
  text: string;
}

const ChatbotWidget: React.FC = () => {
  // 1. KHAI BÁO HOOKS TRƯỚC (BẮT BUỘC)
  const user = useSelector((state: RootState) => state.user);

  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([
    { id: 1, sender: "bot", text: CONFIG.welcomeMessage },
  ]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Tự động cuộn xuống cuối khi có tin nhắn mới
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isOpen, isLoading]);

  // 2. SAU ĐÓ MỚI KIỂM TRA ĐIỀU KIỆN RETURN (LOGIC ẨN CHATBOT)
  if (!user || Object.keys(user).length === 0) {
    return null;
  }

  const toggleChat = () => setIsOpen(!isOpen);

  // --- LOGIC XỬ LÝ TRUNG TÂM ---
  const handleSendMessage = async (textOverride?: string) => {
    const userText = textOverride || input;
    if (!userText.trim()) return;

    // Hiển thị tin nhắn người dùng
    const newUserMsg: Message = {
      id: Date.now(),
      sender: "user",
      text: userText,
    };
    setMessages((prev) => [...prev, newUserMsg]);
    setInput("");

    // KIỂM TRA LOCAL RULE TRƯỚC
    const lowerText = userText.toLowerCase();
    let localMatch: string | null = null;

    for (const rule of KEYWORD_RULES) {
      if (rule.keywords.some((k) => lowerText.includes(k))) {
        localMatch = STATIC_DATA[rule.key as keyof typeof STATIC_DATA];
        break;
      }
    }

    if (localMatch) {
      setIsLoading(true);
      setTimeout(() => {
        setMessages((prev) => [
          ...prev,
          { id: Date.now() + 1, sender: "bot", text: localMatch! },
        ]);
        setIsLoading(false);
      }, 500);
      return;
    }

    setIsLoading(true);
    try {
      // --- CẬP NHẬT LOGIC LẤY ID VÀ ROLE ---
      // Kiểm tra cấu trúc UserSlice của bạn để lấy đúng trường id và role
      // Thông thường JWT decode sẽ có: user.sub (id), user.role hoặc user.authorities

      // Ví dụ giả định cấu trúc user (bạn cần kiểm tra console.log(user) để chắc chắn):
      const userId = (user as any).id || (user as any).userId || "0";

      // Lấy role, nếu không có thì mặc định là PATIENT (hoặc GUEST)
      // Nếu role trong DB là "ROLE_ADMIN", "ROLE_DOCTOR" -> cắt chuỗi nếu cần
      const userRole = (user as any).role || (user as any).roles || "PATIENT";

      const response = await fetch(CONFIG.apiEndpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          message: userText,
          user_id: String(userId), // Chuyển sang string cho an toàn với Backend
          role: String(userRole), // Gửi thêm Role
        }),
      });
      // --------------------------------------

      const data = await response.json();

      setMessages((prev) => [
        ...prev,
        {
          id: Date.now() + 1,
          sender: "bot",
          text: data.response || "Xin lỗi, tôi không hiểu ý bạn.",
        },
      ]);
    } catch (error) {
      console.error("AI Error:", error);
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now() + 1,
          sender: "bot",
          text: "⚠️ Lỗi kết nối đến AI Server.",
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="fixed bottom-5 right-5 z-50 font-sans">
      {/* Nút mở Chat */}
      {!isOpen && (
        <button
          onClick={toggleChat}
          className={`${CONFIG.themeColor} text-white p-4 rounded-full shadow-lg hover:brightness-110 transition-all w-14 h-14 flex items-center justify-center`}
        >
          <span className="text-2xl">🤖</span>
        </button>
      )}

      {/* Cửa sổ Chat */}
      {isOpen && (
        <div className="animate-fade-in-up flex flex-col w-[350px] h-[500px] bg-white border border-gray-200 rounded-2xl shadow-2xl overflow-hidden">
          {/* Header */}
          <div
            className={`${CONFIG.themeColor} p-4 flex justify-between items-center text-white shadow-md`}
          >
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                🏥
              </div>
              <div>
                <h3 className="font-bold text-sm">{CONFIG.clinicName}</h3>
                <span className="text-xs text-blue-100 flex items-center gap-1">
                  <span className="w-2 h-2 bg-green-400 rounded-full"></span>{" "}
                  Trực tuyến
                </span>
              </div>
            </div>
            <button
              onClick={toggleChat}
              className="hover:bg-white/20 p-1 rounded"
            >
              ✕
            </button>
          </div>

          {/* Body: Messages */}
          <div className="flex-1 overflow-y-auto p-4 bg-gray-50 space-y-3">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex ${
                  msg.sender === "user" ? "justify-end" : "justify-start"
                }`}
              >
                <div
                  className={`max-w-[85%] px-4 py-2 rounded-2xl text-sm shadow-sm ${
                    msg.sender === "user"
                      ? `${CONFIG.themeColor} text-white rounded-br-none`
                      : "bg-white border border-gray-200 text-gray-800 rounded-bl-none"
                  }`}
                >
                  {msg.text}
                </div>
              </div>
            ))}

            {/* Loading Indicator */}
            {isLoading && (
              <div className="flex justify-start">
                <div className="bg-gray-200 text-gray-500 px-3 py-2 rounded-2xl rounded-bl-none text-xs flex gap-1 items-center">
                  <span>AI đang suy nghĩ</span>
                  <span className="animate-bounce">.</span>
                  <span className="animate-bounce delay-100">.</span>
                  <span className="animate-bounce delay-200">.</span>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Quick Replies */}
          <div className="px-4 py-2 bg-white border-t border-gray-100 overflow-x-auto flex gap-2 no-scrollbar">
            {QUICK_REPLIES.map((qr) => (
              <button
                key={qr.action}
                onClick={() => handleSendMessage(qr.text)}
                disabled={isLoading}
                className="whitespace-nowrap px-3 py-1 bg-blue-50 text-blue-600 text-xs rounded-full border border-blue-100 hover:bg-blue-100 transition-colors disabled:opacity-50"
              >
                {qr.text}
              </button>
            ))}
          </div>

          {/* Footer: Input */}
          <div className="p-3 bg-white border-t border-gray-200 flex items-center gap-2">
            <input
              type="text"
              className="flex-1 border border-gray-300 rounded-full px-4 py-2 text-sm outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all"
              placeholder="Nhập câu hỏi..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
              disabled={isLoading}
            />
            <button
              onClick={() => handleSendMessage()}
              disabled={isLoading || !input.trim()}
              className={`${CONFIG.themeColor} text-white p-2 rounded-full hover:brightness-110 disabled:bg-gray-400 transition-all`}
            >
              ➤
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatbotWidget;
