import axiosInstance from "../Interceptor/AxiosInterceptor";

// --- Appointment Services ---

const scheduleAppointment = async (data: any) => {
  const ENDPOINT = process.env.REACT_APP_SCHEDULE_APPOINTMENT_ENDPOINT;
  return axiosInstance
    .post(ENDPOINT!, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const cancelAppointment = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_CANCEL_APPOINTMENT_ENDPOINT;
  return axiosInstance
    .put(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAppointment = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_APPOINTMENT_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAppointmentDetails = async (id: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_APPOINTMENT_DETAILS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAppointmentsByPatient = async (patientId: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_APPOINTMENTS_BY_PATIENT_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + patientId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAppointmentsByDoctor = async (doctorId: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_APPOINTMENTS_BY_DOCTOR_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + doctorId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getTodaysAppointment = async () => {
  const ENDPOINT = process.env.REACT_APP_GET_TODAYS_APPOINTMENT_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// --- Report & Prescription Services ---

const createAppointmentReport = async (data: any) => {
  const ENDPOINT = process.env.REACT_APP_CREATE_APPOINTMENT_REPORT_ENDPOINT;
  return axiosInstance
    .post(ENDPOINT!, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const isReportExists = async (appointmentId: any) => {
  const ENDPOINT = process.env.REACT_APP_IS_REPORT_EXISTS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + appointmentId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getReportsByPatientId = async (patientId: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_REPORTS_BY_PATIENT_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + patientId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getPrescriptionsByPatientId = async (patientId: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_PRESCRIPTIONS_BY_PATIENT_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + patientId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllPrescriptions = async () => {
  const ENDPOINT = process.env.REACT_APP_GET_ALL_PRESCRIPTIONS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getMedicinesByPrescriptionId = async (prescriptionId: any) => {
  const ENDPOINT = process.env.REACT_APP_GET_MEDICINES_BY_PRESCRIPTION_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + prescriptionId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getMedicinesConsumedByPatient = async (patientId: any) => {
  const ENDPOINT =
    process.env.REACT_APP_GET_MEDICINES_CONSUMED_BY_PATIENT_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + patientId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// --- Count Services ---

const countAppointmentsByPatient = async (patientId: any) => {
  const ENDPOINT = process.env.REACT_APP_COUNT_APPOINTMENTS_BY_PATIENT_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + patientId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const countAppointmentsByDoctor = async (doctorId: any) => {
  const ENDPOINT = process.env.REACT_APP_COUNT_APPOINTMENTS_BY_DOCTOR_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + doctorId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const countPatientsByDoctor = async (doctorId: any) => {
  const ENDPOINT = process.env.REACT_APP_COUNT_PATIENTS_BY_DOCTOR_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + doctorId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const countAllAppointments = async () => {
  const ENDPOINT = process.env.REACT_APP_COUNT_ALL_APPOINTMENTS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const countReasonsByPatient = async (patientId: any) => {
  const ENDPOINT = process.env.REACT_APP_COUNT_REASONS_BY_PATIENT_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + patientId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const countReasonsByDoctor = async (doctorId: any) => {
  const ENDPOINT = process.env.REACT_APP_COUNT_REASONS_BY_DOCTOR_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT! + doctorId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const countAllReasons = async () => {
  const ENDPOINT = process.env.REACT_APP_COUNT_ALL_REASONS_ENDPOINT;
  return axiosInstance
    .get(ENDPOINT!)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// --- Schedule Services ---

const createSchedule = async (data: any) => {
  const ENDPOINT = "/appointment/doctor-schedule/create";
  return axiosInstance
    .post(ENDPOINT, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const lockSchedule = async (data: any) => {
  const ENDPOINT = "/appointment/doctor-schedule/lock";
  return axiosInstance
    .post(ENDPOINT, data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const unlockSchedule = async (doctorId: any, scheduleDate: string) => {
  const ENDPOINT = `/appointment/doctor-schedule/unlock/${doctorId}?scheduleDate=${scheduleDate}`;
  return axiosInstance
    .put(ENDPOINT)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getSchedule = async (doctorId: any, scheduleDate: string) => {
  const ENDPOINT = `/appointment/doctor-schedule/get/${doctorId}?scheduleDate=${scheduleDate}`;
  return axiosInstance
    .get(ENDPOINT)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getSchedulesByDoctor = async (doctorId: any, startDate?: string, endDate?: string) => {
  let ENDPOINT = `/appointment/doctor-schedule/getAll/${doctorId}`;
  if (startDate) ENDPOINT += `?startDate=${startDate}`;
  if (endDate) ENDPOINT += startDate ? `&endDate=${endDate}` : `?endDate=${endDate}`;
  return axiosInstance
    .get(ENDPOINT)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

const getAllShifts = async () => {
  const ENDPOINT = "/appointment/doctor-schedule/shifts";
  return axiosInstance
    .get(ENDPOINT)
    .then((response: any) => response.data)
    .catch((error: any) => {
      throw error;
    });
};

// --- Exports ---

export {
  scheduleAppointment,
  cancelAppointment,
  getAppointment,
  getAppointmentDetails,
  getAppointmentsByPatient,
  getAppointmentsByDoctor,
  createAppointmentReport,
  isReportExists,
  getReportsByPatientId,
  getPrescriptionsByPatientId,
  getAllPrescriptions,
  getMedicinesByPrescriptionId,
  countAppointmentsByPatient,
  countAppointmentsByDoctor,
  countAllAppointments,
  countAllReasons,
  countReasonsByDoctor,
  countReasonsByPatient,
  getMedicinesConsumedByPatient,
  getTodaysAppointment,
  countPatientsByDoctor,
  createSchedule,
  lockSchedule,
  unlockSchedule,
  getSchedule,
  getSchedulesByDoctor,
  getAllShifts,
};
