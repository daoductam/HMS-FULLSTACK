import { AreaChart } from "@mantine/charts";
import React, { useEffect, useState } from "react";
import {
  data,
  dataDoctors,
  dataPatients,
  doctors,
  patients,
} from "../../../Data/DashboardData";
import { ThemeIcon } from "@mantine/core";
import {
  IconFileReport,
  IconPhoto,
  IconStethoscope,
  IconUser,
} from "@tabler/icons-react";
import { countAllAppointments } from "../../../Service/AppointmentService";
import {
  addZeroMonths,
  convertToVietnameseMonths,
} from "../../../Utility/OtherUtility";
import { getRegistrationCounts } from "../../../Service/UserService";

const TopCards = () => {
  const [apData, setApData] = useState<any[]>(data);
  const [ptData, setPtData] = useState<any[]>(patients);
  const [drData, setDrData] = useState<any[]>(doctors);

  useEffect(() => {
    countAllAppointments()
      .then((res) => {
        setApData(addZeroMonths(res, "month", "count"));
      })
      .catch((err) => {
        console.log(err);
      });

    getRegistrationCounts()
      .then((res) => {
        setPtData(addZeroMonths(res.patientCounts, "month", "count"));
        setDrData(addZeroMonths(res.doctorCounts, "month", "count"));
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);
  const getSum = (data: any[], key: string) => {
    return data.reduce((sum, item) => sum + item[key], 0);
  };

  const card = (
    name: string,
    id: string,
    color: string,
    bg: string,
    icon: React.ReactNode,
    data: any[]
  ) => {
    return (
      <div className={`${bg} rounded-xl`}>
        <div className="flex justify-between p-5 items-center gap-5">
          <ThemeIcon className="!shadow-xl" size="xl" radius="md" color={color}>
            {icon}
          </ThemeIcon>
          <div className="flex flex-col font-medium items-end">
            <div className="">{name}</div>
            <div className="text-lg">{getSum(data, id)}</div>
          </div>
        </div>
        <AreaChart
          h={100}
          data={data}
          dataKey="month"
          series={[{ name: id, color: color }]}
          strokeWidth={5}
          withGradient
          fillOpacity={0.7}
          curveType="bump"
          tickLine="none"
          gridAxis="none"
          withXAxis={false}
          withYAxis={false}
          withDots={false}
        />
      </div>
    );
  };
  const cards = [
    {
      name: "Cuộc hẹn",
      id: "count",
      color: "violet",
      bg: "bg-violet-100",
      icon: <IconFileReport />,
      data: convertToVietnameseMonths(apData),
    },
    {
      name: "Bệnh nhân",
      id: "count",
      color: "green",
      bg: "bg-green-100",
      icon: <IconUser />,
      data: ptData,
    },
    {
      name: "Bác sĩ",
      id: "count",
      color: "blue",
      bg: "bg-blue-100",
      icon: <IconStethoscope />,
      data: drData,
    },
  ];
  return (
    <div className="grid lg:grid-cols-3 gap-5">
      {cards.map((cardData) =>
        card(
          cardData.name,
          cardData.id,
          cardData.color,
          cardData.bg,
          cardData.icon,
          cardData.data
        )
      )}
    </div>
  );
};

export default TopCards;
