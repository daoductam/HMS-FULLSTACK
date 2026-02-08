import { Avatar, Divider } from "@mantine/core";
import React from "react";
import { formatDate, formatDateWithTime } from "../../../Utility/DateUtility";
import {
  IconBriefcase,
  IconCalendarHeart,
  IconClock,
  IconCurrencyReal,
  IconCurrencyRupee,
  IconEmergencyBed,
  IconMail,
  IconMapPin,
  IconMedicineSyrup,
  IconNote,
  IconPhone,
  IconPill,
  IconProgress,
  IconStack2,
  IconUserHeart,
  IconVaccine,
} from "@tabler/icons-react";
import { Tag } from "primereact/tag";
import { useNavigate } from "react-router-dom";

const InvCard = ({
  id,
  medicineId,
  quantity,
  initialQuantity,
  status,
  batchNo,
  expiryDate,
  medicineMap,
  onEdit,
}: any) => {
  return (
    <div
      onClick={onEdit}
      className="border p-4 flex flex-col gap-2 hover:bg-primary-1 transition duration-300 ease-in-out rounded-xl hover:shadow-[0_0_5px_1px_blue] !shadow-primary-5  cursor-pointer space-y-2 "
    >
      <div className="flex  text-xs items-center gap-2 ">
        <IconPill
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">
          {medicineMap[medicineId]?.name}{" "}
          <span className="text-gray-500">
            ({medicineMap[medicineId]?.manufacturer} )
          </span>
        </div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconPill
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{batchNo} </div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconVaccine
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{formatDate(expiryDate)} </div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconStack2
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">Hàng tồn: {quantity}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconMedicineSyrup
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{status}</div>
      </div>
    </div>
  );
};

export default InvCard;
