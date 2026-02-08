import Welcome from "./Welcome";
import Visits from "./Visits";
import DiseaseChart from "./DiseaseChart";
import Appointments from "./Appointment";
import Medications from "./Medications";

const Dashboard = () => {
  return (
    <div className="flex flex-col gap-5">
      <div className="grid lg:grid-cols-2 gap-5">
        <Welcome />
        <Visits />
      </div>
      <div className="grid lg:grid-cols-3 gap-5">
        <DiseaseChart />
        <Appointments />
        <Medications />
      </div>
    </div>
  );
};

export default Dashboard;
