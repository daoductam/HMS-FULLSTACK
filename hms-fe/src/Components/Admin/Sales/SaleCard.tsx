import { Avatar, Divider } from "@mantine/core";
import React from "react";
import { formatDate, formatDateWithTime } from "../../../Utility/DateUtility";
import {
  IconBriefcase,
  IconCalendarHeart,
  IconClock,
  IconCoinRupee,
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

const SaleCard = ({
  buyerContact,
  saleDate,
  buyerName,
  totalAmount,
  onView,
}: any) => {
  return (
    <div
      onClick={onView}
      className="border p-4 flex flex-col gap-2 hover:bg-primary-1 transition duration-300 ease-in-out rounded-xl hover:shadow-[0_0_5px_1px_blue] !shadow-primary-5  cursor-pointer space-y-2 "
    >
      <div className="flex  text-xs items-center gap-2 ">
        <IconUserHeart
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{buyerName}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconPill
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">+84 {buyerContact}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconCoinRupee
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{totalAmount}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconClock
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{formatDate(saleDate)}</div>
      </div>
    </div>
  );
};

export default SaleCard;
