import { useMediaQuery } from "@mantine/hooks";
import Sidebar from "../Components/Admin/Sidebar/Sidebar";
import Header from "../Components/Header/Header";
import { Outlet } from "react-router-dom";

const AdminDashBoard = () => {
  const matches = useMediaQuery("(max-width: 768px)");
  return (
    <div>
      <div className="flex">
        {!matches && <Sidebar />}
        <div className="w-full flex flex-col">
          <Header />
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default AdminDashBoard;
