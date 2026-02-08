import React from "react";

// URL tới ứng dụng chat đã deploy của bạn
const CHAT_APP_URL = "https://chat-app-frontend-seven-bice.vercel.app/";

const Community: React.FC = () => {
  return (
    <div className="community-container">
      <iframe
        src={CHAT_APP_URL}
        title="Cộng đồng Chat"
        className="chat-iframe"
      />
    </div>
  );
};

export default Community;
