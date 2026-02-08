import { Avatar, Divider } from "@mantine/core";
import React, { useEffect, useState } from "react";
import { formatDate } from "../../../Utility/DateUtility";
import {
  IconBriefcase,
  IconCalendarHeart,
  IconMail,
  IconMapPin,
  IconPhone,
} from "@tabler/icons-react";
import { useSelector } from "react-redux";
import { getUserProfile } from "../../../Service/UserService";
import useProtectedImage from "../../Utility/Dropzone/useProtectedImage";
import { url } from "inspector";

const PatientCard = ({
  name,
  email,
  dob,
  phone,
  id,
  address,
  CCCD,
  bloodGroup,
  allergies,
  chronicDisease,
}: any) => {
  const getAge = (dob: string) => {
    if (!dob) return "N/A";
    const birthDate = new Date(dob);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDifference = today.getMonth() - birthDate.getMonth();

    if (
      monthDifference < 0 ||
      (monthDifference === 0 && today.getDate() < birthDate.getDate())
    ) {
      age--;
    }

    return age.toString();
  };
  console.log(dob);

  const user = useSelector((state: any) => state.user);
  const [picId, setPicId] = useState<string | null>(null);
  useEffect(() => {
    if (!user) return;
    getUserProfile(id)
      .then((data) => {
        setPicId(data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);
  const url = useProtectedImage(picId);

  return (
    <div className="border p-4 flex flex-col gap-2 hover:bg-primary-1 transition duration-300 ease-in-out rounded-xl hover:shadow-[0_0_5px_1px_blue] !shadow-primary-5  cursor-pointer space-y-2 ">
      <div className="flex items-center gap-3 ">
        <Avatar
          size="lg"
          src={url}
          // name={name}
          color="initials"
          variant="filled"
        />
        <div className="">
          <div className="text-sm">{name}</div>
          <div className="text-xs text-gray-500">{bloodGroup}</div>
        </div>
      </div>
      <Divider />
      <div className="flex  text-xs items-center gap-2 ">
        <IconMail
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{email}</div>
      </div>
      {/* <div className="flex justify-between text-xs items-center gap-2">
        <div className="text-gray-600">Ngày sinh:</div>
        <div className="">{formatDate(dob)}</div>
      </div> */}
      <div className="flex  text-xs items-center gap-2 ">
        <IconPhone
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">+84 {phone}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconMapPin
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{address}</div>
      </div>
      <div className="flex  text-xs items-center gap-2 ">
        <IconCalendarHeart
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{formatDate(dob)}</div>
      </div>
      {/* <div className="flex  text-xs items-center gap-2 ">
        <IconBriefcase
          className="text-gray-700 bg-primary-1 p-1 rounded-full"
          size={24}
        />
        <div className="">{totalExp} Năm</div>
      </div> */}
    </div>
  );
};

export default PatientCard;
