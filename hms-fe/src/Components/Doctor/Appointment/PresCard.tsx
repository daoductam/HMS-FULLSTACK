import { Avatar, Button, Divider } from "@mantine/core";
import React from "react";
import { formatDate, formatDateWithTime } from "../../../Utility/DateUtility";
import {
  IconBriefcase,
  IconCalendarHeart,
  IconClock,
  IconEmergencyBed,
  IconMail,
  IconMapPin,
  IconMedicineSyrup,
  IconNote,
  IconPhone,
  IconProgress,
  IconUserHeart,
} from "@tabler/icons-react";
import { Tag } from "primereact/tag";
import { useNavigate } from "react-router-dom";

const PresCard = ({
  appointmentId,
  doctorName,
  notes,
  reason,
  status,
  prescriptionDate,
  medicines,
  handleMedicine,
}: any) => {
  const navigate = useNavigate();
  const getSeverity = (status: string) => {
    switch (status) {
      case "CANCELLED":
        return "danger";

      case "COMPLETED":
        return "success";

      case "SCHEDULED":
        return "info";

      case "negotiation":
        return "warning";

      default:
        return null;
    }
  };
  return (
    <div
      onClick={() => navigate("/doctor/appointments/" + appointmentId)}
      className="border p-4 flex flex-col gap-2 hover:bg-primary-1 transition duration-300 ease-in-out rounded-xl hover:shadow-[0_0_5px_1px_blue] !shadow-primary-5  cursor-pointer space-y-2 "
    >
      <div className="flex  text-xs items-center gap-2 ">
        <IconUserHeart
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{doctorName}</div>
      </div>

      <div className="flex  text-xs items-center gap-2 ">
        <IconClock
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{formatDate(prescriptionDate)}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconMedicineSyrup
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="flex items-center gap-2 ">
          {medicines.length}{" "}
          <Button
            onClick={() => handleMedicine(medicines)}
            className="compact-xs"
          >
            Xem Thuốc
          </Button>
        </div>
      </div>
      {notes && (
        <div className="flex  text-xs items-center gap-2 ">
          <IconNote
            className="text-gray-700 bg-primary-1 p-1 rounded-full"
            size={24}
          />
          <div className="">{notes}</div>
        </div>
      )}
    </div>
  );
};

export default PresCard;
