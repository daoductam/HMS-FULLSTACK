// src/Pages/Admin/AdminEditPatientPage.tsx
import AdminEditPatientProfile from "../../Components/Admin/Patients/AdminEditPatientProfile";

const AdminEditPatientPage = () => {
  return (
    <div>
      {/* Trang này chỉ đơn giản là gọi component Form
        Vì nó nằm trong AdminDashBoard nên sẽ có Sidebar + Header
      */}
      <AdminEditPatientProfile />
    </div>
  );
};

export default AdminEditPatientPage;
