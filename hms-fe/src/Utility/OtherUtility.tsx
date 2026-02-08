const arrayToCSV = (arr: String[]) => {
  if (!arr || arr.length === 0) return null;
  return arr.join(", ");
};

// const capitalizeFirstLetter = (string:string) => {
//   if(!string) return string;
//   return string
// }

const getLabel = (dataList: any, value: any) => {
  const found = dataList.find((item: any) => item.value === value);
  return found ? found.label : value;
};

const addZeroMonths = (data: any[], monthKey: string, valueKey: string) => {
  const month = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
  ];
  const result = month.map((month) => {
    const found = data.find((item) => item[monthKey] === month);
    return found ? found : { [monthKey]: month, [valueKey]: 0 };
  });
  return result;
};

const englishToVietnamese = {
  January: "Tháng 1",
  February: "Tháng 2",
  March: "Tháng 3",
  April: "Tháng 4",
  May: "Tháng 5",
  June: "Tháng 6",
  July: "Tháng 7",
  August: "Tháng 8",
  September: "Tháng 9",
  October: "Tháng 10",
  November: "Tháng 11",
  December: "Tháng 12",
};

const convertToVietnameseMonths = (data: any[]) => {
  return Object.entries(englishToVietnamese).map(([eng, vi]) => {
    const found = data.find((item) => item.month === eng);
    return found ? { month: vi, count: found.count } : { month: vi, count: 0 };
  });
};

const convertReasonChartData = (data: any[]) => {
  const colors = [
    "#4caf50",
    "#2196f3",
    "#ff9800",
    "#f44336",
    "#9c27b0",
    "#3f51b5",
    "#00bcd4",
    "#8bc34a",
    "#ffc107",
    "#e91e63",
  ];
  return data.map((item, index) => ({
    name: item.reason,
    value: item.count,
    color: colors[index % colors.length], // Cycle through colors if more reasons than colors
  }));
};

const extractTimeIn12HourFormat = (dateString: any) => {
  if (!dateString) return undefined;

  const date = new Date(dateString);

  const options: Intl.DateTimeFormatOptions = {
    hour: "numeric",
    minute: "numeric",
    hour12: true,
  };

  return date.toLocaleString("en-US", options);
};

export {
  arrayToCSV,
  getLabel,
  addZeroMonths,
  convertToVietnameseMonths,
  convertReasonChartData,
  extractTimeIn12HourFormat,
};
