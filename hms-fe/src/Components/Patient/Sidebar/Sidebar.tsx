import { Avatar, Text } from "@mantine/core";
import {
  IconBuildingCommunity,
  IconCalendarCheck,
  IconHeartbeat,
  IconLayoutGrid,
  IconMoodHeart,
  IconUser,
} from "@tabler/icons-react";
import { useEffect, useState } from "react";

import { useSelector } from "react-redux";
import { NavLink } from "react-router-dom";
import useProtectedImage from "../../Utility/Dropzone/useProtectedImage";
import { getUserProfile } from "../../../Service/UserService";

const links = [
  {
    name: "Trang tổng quan",
    url: "/patient/dashboard",
    icon: <IconLayoutGrid stroke={1.5} />,
  },
  {
    name: "Hồ sơ",
    url: "/patient/profile",
    icon: <IconUser stroke={1.5} />,
  },
  {
    name: "Bác sĩ",
    url: "/patient/doctors",
    icon: <IconMoodHeart stroke={1.5} />,
  },
  {
    name: "Lịch hẹn",
    url: "/patient/appointments",
    icon: <IconCalendarCheck stroke={1.5} />,
  },
  {
    name: "Cộng Đồng",
    url: "/patient/community",
    icon: <IconBuildingCommunity stroke={1.5} />,
  },
];

const Sidebar = () => {
  const user = useSelector((state: any) => state.user);

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
  }, []);
  const url = useProtectedImage(picId);

  return (
    <div className="flex">
      <div className="w-64"></div>
      <div className="w-64 fixed h-screen overflow-y-auto hide-scrollbar bg-dark flex flex-col gap-7 items-center shadow-2xl animate-slide-in-left">
        <div className="fixed z-[500] py-3 bg-dark text-primary-4 flex gap-1 items-center animate-fade-in-down">
          <IconHeartbeat
            size={40}
            stroke={2.5}
            className="animate-pulse-subtle"
          />
          <span className="font-heading font-semibold text-3xl">TPVK</span>
        </div>
        <div className="flex flex-col mt-20 gap-5 w-full px-3">
          <div className="flex flex-col gap-1 items-center animate-fade-in">
            <div className="p-1 bg-white rounded-full shadow-lg hover-scale cursor-pointer">
              <Avatar
                variant="filled"
                src={url}
                size="xl"
                alt="it's me"
              ></Avatar>
            </div>
            <span className="font-medium text-light mt-2">{user.name}</span>
            <Text c="dimmed" className="text-light" size="xs">
              {user.role}
            </Text>
          </div>
          <div className="flex flex-col gap-2">
            {links.map((link, index) => {
              return (
                <NavLink
                  to={link.url}
                  key={link.url}
                  style={{ animationDelay: `${index * 0.05}s` }}
                  className={({ isActive }) =>
                    `flex items-center gap-3 w-full font-medium text-light px-4 py-3 rounded-lg smooth-transition animate-fade-in-up ${
                      isActive
                        ? "bg-primary-4 text-dark shadow-lg scale-105"
                        : "hover:bg-gray-100 hover:text-dark hover:translate-x-1 hover:shadow-md"
                    }`
                  }
                >
                  {link.icon}
                  <span className="text-sm">{link.name}</span>
                </NavLink>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
