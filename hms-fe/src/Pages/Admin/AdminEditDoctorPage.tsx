// src/Pages/Admin/AdminEditPatientPage.tsx
import AdminEditDoctorProfile from "../../Components/Admin/Doctors/AdminEditDoctorProfile";
import AdminEditPatientProfile from "../../Components/Admin/Patients/AdminEditPatientProfile";

const AdminEditDoctorPage = () => {
  return (
    <div>
      {/* Trang này chỉ đơn giản là gọi component Form
        Vì nó nằm trong AdminDashBoard nên sẽ có Sidebar + Header
      */}
      <AdminEditDoctorProfile />
    </div>
  );
};

export default AdminEditDoctorPage;
