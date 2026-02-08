import { Avatar } from "@mantine/core";
import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { getUserProfile } from "../../../Service/UserService";
import useProtectedImage from "../../Utility/Dropzone/useProtectedImage";
import { getPatient } from "../../../Service/PatientProfileService";

const Welcome = () => {
  const user = useSelector((state: any) => state.user);
  const [patientInfo, setPatientInfo] = useState<any>({});

  const [picId, setPicId] = useState<string | null>(null);
  useEffect(() => {
    if (!user) return;
    getUserProfile(user.id)
      .then((data) => {
        setPicId(data);
      })
      .catch((error) => {
        console.log(error);
      });
    getPatient(user.profileId)
      .then((data) => {
        setPatientInfo(data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);
  const url = useProtectedImage(picId);
  return (
    <div className="p-5 border shadow-sm rounded-xl bg-blue-50 flex flex-col gap-3">
      <div className="flex justify-between items-center">
        <div className="">
          <div className="">Chào mừng trở lại</div>
          <div className="text-3xl font-semibold text-blue-600">
            {user.name}
          </div>
          <div className="text-sm">
            {patientInfo.bloodGroup}, {patientInfo.address}
          </div>
        </div>
        <Avatar variant="filled" src={url} size={100} alt="it's me"></Avatar>
      </div>
      <div className="flex gap-5">
        <div className="p-3 rounded-xl bg-violet-200">
          <div className="text-sm">Số lần khám bệnh</div>
          <div className="text-lg font-semibold text-violet-600">120+</div>
        </div>
        <div className="p-3 rounded-xl bg-orange-200">
          <div className="text-sm">Bệnh nhân</div>
          <div className="text-lg font-semibold text-orange-600">120+</div>
        </div>
      </div>
    </div>
  );
};

export default Welcome;
