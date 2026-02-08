import { AreaChart } from "@mantine/charts";
import React, { useEffect, useState } from "react";
import {
  data,
  dataDoctors,
  dataPatients,
  medicines,
} from "../../../Data/DashboardData";
import { Box, ScrollArea, ThemeIcon } from "@mantine/core";
import {
  IconFileReport,
  IconPhoto,
  IconStethoscope,
  IconUser,
} from "@tabler/icons-react";
import { getAllMedicines } from "../../../Service/MedicineService";

const Medicines = () => {
  const [data, setData] = useState<any[]>(medicines);
  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = () => {
    // if (!appointment?.patientId) return;

    getAllMedicines()
      .then((res) => {
        console.log("Report Data: ", res);
        setData(res);
      })
      .catch((err) => {
        console.log("Error fetching request: ", err);
      });
  };
  const card = (app: any) => {
    return (
      <div
        className="p-3 mb-3 border rounded-xl justify-between border-l-4
         border-orange-500 shadow-md flex bg-orange-100"
        key={app.id}
      >
        <div className="">
          <div className="font-semibold text-sm">{app.name}</div>
          <div className="text-xs text-gray-500">{app.manufacturer}</div>
        </div>
        <div className="text-right">
          <div className="text-xs text-gray-500">{app.dosage}</div>
          <div className="text-xs text-gray-500">Hàng tồn: {app.stock}</div>
        </div>
      </div>
    );
  };

  return (
    <div className="p-3 border rounded-xl bg-orange-50 shadow-xl flex flex-col gap-3">
      <div className="text-xl font-semibold">Thuốc</div>
      <div className="">
        <ScrollArea.Autosize mah={300} mx="auto">
          {data.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default Medicines;
