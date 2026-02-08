import { AreaChart } from "@mantine/charts";
import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { countPatientsByDoctor } from "../../../Service/AppointmentService";
import { addZeroMonths } from "../../../Utility/OtherUtility";
const PatientMetrics = () => {
  const [appointments, setAppointments] = useState<any[]>([]);
  const user = useSelector((state: any) => state.user);
  useEffect(() => {
    countPatientsByDoctor(user.profileId)
      .then((res) => {
        setAppointments(addZeroMonths(res, "month", "count"));
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);
  const data = [
    { date: "January", patients: 5 },
    { date: "February", patients: 8 },
    { date: "March", patients: 6 },
    { date: "April", patients: 10 },
    { date: "May", patients: 7 },
    { date: "June", patients: 9 },
    { date: "July", patients: 11 },
    { date: "August", patients: 8 },
    { date: "September", patients: 6 },
    { date: "October", patients: 12 },
    { date: "November", patients: 9 },
    { date: "December", patients: 14 },
  ];
  const getSum = (data: any[], key: string) => {
    return data.reduce((sum, item) => sum + item[key], 0);
  };
  return (
    <div className="bg-orange-50 rounded-xl border">
      <div className="flex justify-between p-5 items-center">
        <div className="">
          <div className="font-semibold">Bệnh nhân</div>
          <div className="text-xs text-gray-500">
            {new Date().getFullYear()}
          </div>
        </div>

        <div className="text-2xl font-bold text-orange-500">
          {getSum(appointments, "count")}
        </div>
      </div>
      <AreaChart
        h={280}
        data={appointments}
        dataKey="month"
        series={[{ name: "count", color: "orange" }]}
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

export default PatientMetrics;
