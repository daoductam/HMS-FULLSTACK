import React, { useEffect, useState } from "react";
import { ScrollArea } from "@mantine/core";
import { useSelector } from "react-redux";
import { getAppointmentsByPatient } from "../../../Service/AppointmentService";
import { formatDate } from "../../../Utility/DateUtility";
import { extractTimeIn12HourFormat } from "../../../Utility/OtherUtility";

const Appointments = () => {
  const user = useSelector((state: any) => state.user);
  const [appointments, setAppointments] = useState<any[]>([]);

  useEffect(() => {
    getAppointmentsByPatient(user.profileId)
      .then((res) => {
        setAppointments(res);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  const card = (app: any) => {
    return (
      <div
        className="p-3 mb-3 border rounded-xl justify-between border-l-4
         border-blue-500 shadow-md flex bg-blue-100 items-center"
        key={app.id}
      >
        <div className=" ">
          <div className="font-semibold">{app.doctorName}</div>
          {/* <div className="text-sm text-gray-500">{app.doctor}</div> */}
          <div className="text-sm text-gray-500">{app.reason}</div>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-500">
            {formatDate(app.appointmentTime)}
          </div>

          <div className="text-sm text-gray-500">
            {extractTimeIn12HourFormat(app.appointmentTime)}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="p-3 border rounded-xl bg-blue-50 shadow-xl flex flex-col gap-3">
      <div className="text-xl font-semibold">Cuộc hẹn</div>
      <div className="">
        <ScrollArea.Autosize mah={300} maw={400} mx="auto">
          {appointments.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default Appointments;
