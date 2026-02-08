import { Avatar, Divider } from "@mantine/core";
import React from "react";
import { formatDate, formatDateWithTime } from "../../../Utility/DateUtility";
import {
  IconBriefcase,
  IconCalendarHeart,
  IconClock,
  IconEmergencyBed,
  IconMail,
  IconMapPin,
  IconNote,
  IconPhone,
  IconProgress,
  IconUserHeart,
} from "@tabler/icons-react";
import { Tag } from "primereact/tag";

const ApCard = ({
  id,
  doctorName,
  doctorId,
  notes,
  reason,
  status,
  appointmentTime,
}: any) => {
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
    <div className="border p-4 flex flex-col gap-2 hover:bg-primary-1 transition duration-300 ease-in-out rounded-xl hover:shadow-[0_0_5px_1px_blue] !shadow-primary-5  cursor-pointer space-y-2 ">
      <div className="flex  text-xs items-center gap-2 ">
        <IconUserHeart
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{doctorName}</div>
      </div>
      {/* <div className="flex justify-between text-xs items-center gap-2">
        <div className="text-gray-600">Ngày sinh:</div>
        <div className="">{formatDate(dob)}</div>
      </div> */}
      <div className="flex  text-xs items-center gap-2 ">
        <IconNote
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{notes}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconEmergencyBed
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{reason}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconClock
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{formatDateWithTime(appointmentTime)}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconProgress
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <Tag value={status} severity={getSeverity(status)} />
      </div>
    </div>
  );
};

export default ApCard;
