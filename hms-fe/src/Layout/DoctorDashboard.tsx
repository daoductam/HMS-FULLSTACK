import { useMediaQuery } from "@mantine/hooks";
import Sidebar from "../Components/Doctor/Sidebar/Sidebar";
import Header from "../Components/Header/Header";
import { Outlet } from "react-router-dom";

const DoctorDashBoard = () => {
  const matches = useMediaQuery("(max-width: 768px)");
  return (
    <div>
      <div className="flex">
        {!matches && <Sidebar />}
        <div className="w-full overflow-hidden flex flex-col">
          <Header />
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default DoctorDashBoard;
