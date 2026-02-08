import Sidebar from "../Components/Patient/Sidebar/Sidebar";
import Header from "../Components/Header/Header";
import { Outlet } from "react-router-dom";
import { useMediaQuery } from "@mantine/hooks";

const PatientDashBoard = () => {
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

export default PatientDashBoard;
