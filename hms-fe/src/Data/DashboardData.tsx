const data = [
  { date: "Jan 2025", appointments: 54 },
  { date: "Feb 2025", appointments: 88 },
  { date: "Mar 2025", appointments: 41 },
  { date: "Apr 2025", appointments: 97 },
  { date: "May 2025", appointments: 76 },
  { date: "Jun 2025", appointments: 32 },
  { date: "Jul 2025", appointments: 115 },
  { date: "Aug 2025", appointments: 68 },
  { date: "Sep 2025", appointments: 82 },
  { date: "Oct 2025", appointments: 59 },
  { date: "Nov 2025", appointments: 103 },
  { date: "Dec 2025", appointments: 47 },
];

const dataPatients = [
  { date: "Jan 2025", patients: 120 },
  { date: "Feb 2025", patients: 132 },
  { date: "Mar 2025", patients: 98 },
  { date: "Apr 2025", patients: 143 },
  { date: "May 2025", patients: 157 },
  { date: "Jun 2025", patients: 121 },
  { date: "Jul 2025", patients: 176 },
  { date: "Aug 2025", patients: 134 },
  { date: "Sep 2025", patients: 142 },
  { date: "Oct 2025", patients: 128 },
  { date: "Nov 2025", patients: 165 },
  { date: "Dec 2025", patients: 111 },
];

const dataDoctors = [
  { date: "Jan 2025", doctors: 22 },
  { date: "Feb 2025", doctors: 24 },
  { date: "Mar 2025", doctors: 25 },
  { date: "Apr 2025", doctors: 27 },
  { date: "May 2025", doctors: 28 },
  { date: "Jun 2025", doctors: 26 },
  { date: "Jul 2025", doctors: 29 },
  { date: "Aug 2025", doctors: 31 },
  { date: "Sep 2025", doctors: 33 },
  { date: "Oct 2025", doctors: 35 },
  { date: "Nov 2025", doctors: 36 },
  { date: "Dec 2025", doctors: 34 },
];

const diseaseData = [
  { name: "Cảm cúm", value: 45, color: "violet" },
  { name: "Tiểu đường", value: 30, color: "blue" },
  { name: "Huyết áp cao", value: 20, color: "green" },
  { name: "COVID-19", value: 5, color: "red" },
];

const appointments = [
  {
    time: "09:00 AM",
    patient: "John Doe",
    reason: "General Checkup",
    doctor: "Dr. Smith",
  },
  {
    time: "10:00 AM",
    patient: "Jane Smith",
    reason: "Dental Cleaning",
    doctor: "Dr. Brown",
  },
  {
    time: "11:00 AM",
    patient: "Mike Johnson",
    reason: "Eye Exam",
    doctor: "Dr. Green",
  },
  {
    time: "01:00 PM",
    patient: "Emily Davis",
    reason: "Physical Therapy",
    doctor: "Dr. White",
  },
  {
    time: "02:00 PM",
    patient: "Chris Wilson",
    reason: "Vaccination",
    doctor: "Dr. Black",
  },
];

const medicines = [
  {
    name: "Paracetamol",
    dosage: "500mg",
    stock: 100,
    manufacturer: "Pharma Inc.",
  },
  {
    name: "Ibuprofen",
    dosage: "200mg",
    stock: 50,
    manufacturer: "HealthCorp.",
  },
  {
    name: "Amoxicillin",
    dosage: "250mg",
    stock: 75,
    manufacturer: "MediLife.",
  },
  {
    name: "Cetirizine",
    dosage: "10mg",
    stock: 30,
    manufacturer: "AllergyFree.",
  },
  {
    name: "Azithromycin",
    dosage: "500mg",
    stock: 20,
    manufacturer: "Antibiotics Co.",
  },
];

const patients = [
  {
    name: "John Doe",
    email: "john@example.com",
    location: "New York",
    bloodGroup: "A+",
  },
  {
    name: "Jane Smith",
    email: "jane@example.com",
    location: "Los Angeles",
    bloodGroup: "B+",
  },
  {
    name: "Mike Johnson",
    email: "mike@example.com",
    location: "Chicago",
    bloodGroup: "O-",
  },
  {
    name: "Emily Davis",
    email: "emily@example.com",
    location: "Houston",
    bloodGroup: "AB+",
  },
];

const doctors = [
  {
    name: "John Doe",
    email: "john@example.com",
    location: "New York",
    department: "Cardiology",
  },
  {
    name: "Jane Smith",
    email: "jane@example.com",
    location: "Los Angeles",
    department: "Neurology",
  },
  {
    name: "Mike Johnson",
    email: "mike@example.com",
    location: "Chicago",
    department: "Pediatrics",
  },
  {
    name: "Emily Davis",
    email: "emily@example.com",
    location: "Houston",
    department: "Orthopedics",
  },
];
export {
  data,
  dataDoctors,
  dataPatients,
  diseaseData,
  appointments,
  medicines,
  patients,
  doctors,
};
