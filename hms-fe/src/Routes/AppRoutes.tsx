import { BrowserRouter, Route, Routes } from "react-router-dom";
import Random from "../Components/Random";
import AdminDashBoard from "../Layout/AdminDashBoard";
import LoginPage from "../Pages/LoginPage";
import RegisterPage from "../Pages/RegisterPage";
import PublicRoutes from "./PublicRoutes";
import ProtectedRoutes from "./ProtectedRoutes";
import PatientDashBoard from "../Layout/PatientDashboard";
import PatientProfilePage from "../Pages/Patient/PatientProfilePage";
import DoctorDashBoard from "./../Layout/DoctorDashboard";
import DoctorProfilePage from "../Pages/Doctor/DoctorProfilePage";
import PatientAppointmentPage from "../Pages/Patient/PatientAppointmentPage";
import DoctorAppointmentPage from "../Pages/Doctor/DoctorAppointmentPage";
import DoctorAppointmentDetailsPage from "../Pages/Doctor/DoctorAppointmentDetailsPage";
import AdminMedicinePage from "../Pages/Admin/AdminMedicinePage";
import NotFoundPage from "../Pages/NotFoundPage";
import AdminInventoryPage from "../Pages/Admin/AdminInventoryPage";
import AdminSalesPage from "../Pages/Admin/AdminSalesPage";
import AdminPatientPage from "../Pages/Admin/AdminPatientPage";
import AdminDoctorPage from "../Pages/Admin/AdminDoctorPage";
import AdminDashboardPage from "../Pages/Admin/AdminDashboardPage";
import DoctorDashboardPage from "../Pages/Doctor/DoctorDashboardPage";
import PatientDashboardPage from "../Pages/Patient/PatientDashboardPage";
import DoctorPatientPage from "../Pages/Doctor/DoctorPatientPage";
import DoctorPharmacyPage from "../Pages/Doctor/DoctorPharmacyPage";
import AdminEditPatientPage from "../Pages/Admin/AdminEditPatientPage";
import AdminEditDoctorProfile from "../Components/Admin/Doctors/AdminEditDoctorProfile";
import AdminEditDoctorPage from "../Pages/Admin/AdminEditDoctorPage";
import AdminSchedulePage from "../Pages/Admin/AdminSchedulePage";
import PatientDoctorPage from "../Pages/Doctor/PatientDoctorPage";
import AdminPendingDoctorsPage from "../Pages/Admin/AdminPendingDoctorsPage";
import PaymentPage from "../Pages/Admin/PaymentPage";
import PaymentResultPage from "../Pages/Admin/PaymentResultPage";
import LandingPage from "../Pages/LandingPage";
const AppRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            <PublicRoutes>
              <LandingPage />
            </PublicRoutes>
          }
        />
        <Route
          path="/register"
          element={
            <PublicRoutes>
              <RegisterPage />
            </PublicRoutes>
          }
        />
        <Route
          path="/login"
          element={
            <PublicRoutes>
              <LoginPage />
            </PublicRoutes>
          }
        />
        <Route
          path="/admin"
          element={
            <ProtectedRoutes>
              <AdminDashBoard />
            </ProtectedRoutes>
          }
        >
          <Route path="dashboard" element={<AdminDashboardPage />} />
          <Route path="medicine" element={<AdminMedicinePage />} />
          <Route path="inventory" element={<AdminInventoryPage />} />

          <Route path="sales" element={<AdminSalesPage />} />
          <Route path="payment" element={<PaymentPage />} />
          <Route path="payment-result" element={<PaymentResultPage />} />

          <Route path="patients" element={<AdminPatientPage />} />
          <Route
            path="/admin/patient/edit/:id" // :id là phần động
            element={<AdminEditPatientPage />}
          />
          <Route path="doctors" element={<AdminDoctorPage />} />
          <Route
            path="/admin/pending-doctors"
            element={<AdminPendingDoctorsPage />}
          />
          <Route
            path="/admin/doctor/edit/:id" // :id là phần động
            element={<AdminEditDoctorPage />}
          />
          <Route path="schedule" element={<AdminSchedulePage />} />
        </Route>

        <Route
          path="/patient"
          element={
            <ProtectedRoutes>
              <PatientDashBoard />
            </ProtectedRoutes>
          }
        >
          <Route path="dashboard" element={<PatientDashboardPage />} />
          <Route path="profile" element={<PatientProfilePage />} />
          <Route path="doctors" element={<PatientDoctorPage />} />
          <Route path="appointments" element={<PatientAppointmentPage />} />
        </Route>

        <Route
          path="/doctor"
          element={
            <ProtectedRoutes>
              <DoctorDashBoard />
            </ProtectedRoutes>
          }
        >
          <Route path="dashboard" element={<DoctorDashboardPage />} />
          <Route path="profile" element={<DoctorProfilePage />} />
          <Route path="appointments" element={<DoctorAppointmentPage />} />
          <Route
            path="appointments/:id"
            element={<DoctorAppointmentDetailsPage />}
          />
          <Route path="pharmacy" element={<DoctorPharmacyPage />} />
          <Route path="patients" element={<DoctorPatientPage />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
};

export default AppRoutes;
