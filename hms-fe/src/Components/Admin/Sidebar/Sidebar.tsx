import { Avatar, Text } from "@mantine/core";
import {
  IconAlt,
  IconBuildingCommunity,
  IconCalendarCheck,
  IconCalendarTime,
  IconHeartbeat,
  IconLayoutGrid,
  IconMoodHeart,
  IconPackage,
  IconReceiptBitcoin,
  IconReceiptRefund,
  IconReceiptRupee,
  IconStethoscope,
  IconUser,
  IconVaccine,
} from "@tabler/icons-react";
import { url } from "inspector";
import React, { use } from "react";
import { useSelector } from "react-redux";
import { NavLink } from "react-router-dom";

const links = [
  {
    name: "Trang tổng quan",
    url: "/admin/dashboard",
    icon: <IconLayoutGrid stroke={1.5} />,
  },
  {
    name: "Bệnh nhân",
    url: "/admin/patients",
    icon: <IconMoodHeart stroke={1.5} />,
  },
  {
    name: "Bác sĩ",
    url: "/admin/doctors",
    icon: <IconMoodHeart stroke={1.5} />,
  },
  {
    name: "Đăng Ký Bác sĩ",
    url: "/admin/pending-doctors",
    icon: <IconMoodHeart stroke={1.5} />,
  },
  {
    name: "Thuốc",
    url: "/admin/medicine",
    icon: <IconVaccine stroke={1.5} />,
  },
  {
    name: "Kho",
    url: "/admin/inventory",
    icon: <IconPackage stroke={1.5} />,
  },
  {
    name: "Bán Hàng",
    url: "/admin/sales",
    icon: <IconReceiptRupee stroke={1.5} />,
  },
  {
    name: "Lịch Làm Việc",
    url: "/admin/schedule",
    icon: <IconCalendarTime stroke={1.5} />,
  },
  {
    name: "Cộng Đồng",
    url: "/admin/community",
    icon: <IconBuildingCommunity stroke={1.5} />,
  },
];

const Sidebar = () => {
  const user = useSelector((state: any) => state.user);
  return (
    <div className="flex">
      <div className="w-64"></div>
      <div className="w-64 fixed h-screen overflow-y-auto hide-scrollbar bg-dark flex flex-col gap-7 items-center ">
        <div className="fixed z-[500] py-3 bg-dark text-primary-4 flex gap-1 items-center ">
          <IconHeartbeat size={40} stroke={2.5} />
          <span className="font-heading font-semibold text-3xl">Pulse</span>
        </div>
        <div className="flex flex-col mt-20 gap-5">
          <div className="flex flex-col gap-1 items-center">
            <div className="p-1 bg-white rounded-full shadow-lg">
              <Avatar
                variant="filled"
                src="/image.png"
                size="xl"
                alt="it's me"
              ></Avatar>
            </div>
            <span className="font-medium text-light">{user.name}</span>
            <Text c="dimmed" className="text-light" size="xs">
              {user.role}
            </Text>
          </div>
          <div className="flex flex-col gap-1">
            {links.map((link) => {
              return (
                <NavLink
                  to={link.url}
                  key={link.url}
                  className={({ isActive }) =>
                    `flex items-center gap-3 w-full font-medium text-light px-4 py-5 rounded-lg ${
                      isActive
                        ? "bg-primary-4 text-dark"
                        : "hover:bg-gray-100 hover:text-dark "
                    }`
                  }
                >
                  {link.icon}
                  <span>{link.name}</span>
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
