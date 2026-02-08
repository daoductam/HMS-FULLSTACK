const formatDate = (dateString: any) => {
  if (!dateString) return undefined;
  const date = new Date(dateString);
  const day = String(date.getDate()).padStart(2, "0");
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
};

const formatDateWithTime = (dateString: string) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString("vi-VN", {
    weekday: "long", // Thứ Tư
    year: "numeric", // 2025
    month: "long", // tháng 5
    day: "numeric", // 14
    hour: "numeric", // 3
    minute: "2-digit", // 30
    hour12: true, // CH / SA
  });
};

/**
 * Format Date object to local datetime string (YYYY-MM-DDTHH:mm:ss)
 * Without timezone conversion - preserves local time
 * Example: 2025-12-24T07:34:00 (not 2025-12-24T00:34:00.000Z)
 */
const formatDateTimeToLocalString = (date: Date): string => {
  if (!date) return "";
  
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");
  
  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
};

export { formatDate, formatDateWithTime, formatDateTimeToLocalString };
