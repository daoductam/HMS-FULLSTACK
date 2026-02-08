import { AreaChart } from "@mantine/charts";
import React, { useEffect, useState } from "react";
import {
  data,
  dataDoctors,
  dataPatients,
  patients,
} from "../../../Data/DashboardData";
import { Box, ScrollArea, ThemeIcon } from "@mantine/core";
import {
  IconFileReport,
  IconPhoto,
  IconStethoscope,
  IconUser,
} from "@tabler/icons-react";
import { getAllPatients } from "../../../Service/PatientProfileService";
import { bloodGroups } from "../../../Data/DropDownData";

const Patients = () => {
  const [patients, setPatients] = useState<any[]>([]);
  useEffect(() => {
    getAllPatients()
      .then((data) => {
        console.log(data);
        setPatients(data);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);
  const card = (app: any) => {
    return (
      <div
        className="p-3 mb-3 border rounded-xl justify-between border-l-4
         border-orange-500 shadow-md flex bg-orange-100"
        key={app.id}
      >
        <div className="">
          <div className="font-semibold text-sm">{app.name}</div>
          <div className="text-xs text-gray-500">{app.email}</div>
        </div>
        <div className="text-right">
          <div className="text-xs text-gray-500">{app.address}</div>
          <div className="text-xs text-gray-500">
            Nhóm máu: {app.bloodGroup}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="p-3 border rounded-xl bg-orange-50 shadow-xl flex flex-col gap-3">
      <div className="text-xl font-semibold">Bệnh nhân</div>
      <div className="">
        <ScrollArea.Autosize mah={300} mx="auto">
          {patients.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default Patients;
