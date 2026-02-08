import { AreaChart } from "@mantine/charts";
import React, { useEffect, useState } from "react";
import { data, doctors } from "../../../Data/DashboardData";
import { Box, ScrollArea, ThemeIcon } from "@mantine/core";
import {
  IconFileReport,
  IconPhoto,
  IconStethoscope,
  IconUser,
} from "@tabler/icons-react";
import { getAllDoctors } from "../../../Service/DoctorProfileService";

const Doctors = () => {
  const [doctors, setDoctors] = useState<any[]>([]);
  useEffect(() => {
    getAllDoctors()
      .then((data) => {
        console.log(data);
        setDoctors(data);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);
  const card = (app: any) => {
    return (
      <div
        className="p-3 mb-3 border rounded-xl justify-between border-l-4
         border-violet-500 shadow-md flex bg-violet-100"
        key={app.id}
      >
        <div className="">
          <div className="font-semibold text-sm">{app.name}</div>
          <div className="text-xs text-gray-500">{app.email}</div>
        </div>
        <div className="text-right">
          <div className="text-xs text-gray-500">{app.address}</div>
          <div className="text-xs text-gray-500">{app.department}</div>
        </div>
      </div>
    );
  };

  return (
    <div className="p-3 border rounded-xl bg-violet-50 shadow-xl flex flex-col gap-3">
      <div className="text-xl font-semibold">Bác sĩ</div>
      <div className="">
        <ScrollArea.Autosize mah={300} mx="auto">
          {doctors.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default Doctors;
