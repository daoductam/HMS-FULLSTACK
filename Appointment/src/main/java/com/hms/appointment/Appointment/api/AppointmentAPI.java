package com.hms.appointment.Appointment.api;

import com.hms.appointment.Appointment.dto.*;
import com.hms.appointment.Appointment.service.AppointmentService;
import com.hms.appointment.Appointment.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@CrossOrigin
@Validated
@RequiredArgsConstructor
public class AppointmentAPI {

    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;

    @PostMapping("/schedule")
    public ResponseEntity<Long> scheduleAppointment(@RequestBody AppointmentDTO appointmentDTO)  {
        return new ResponseEntity<>(appointmentService.scheduleAppointment(appointmentDTO),
                HttpStatus.CREATED);

    }

    @GetMapping("/get/{appointmentId}")
    public ResponseEntity<AppointmentDTO> getAppointmentDetails(@PathVariable Long appointmentId){
        return new ResponseEntity<>(appointmentService.getAppointmentDetails(appointmentId), HttpStatus.OK);

    }

    @PutMapping("/cancel/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId){
        appointmentService.cancelAppointment(appointmentId);
        return new ResponseEntity<>("Cuộc hẹn đã hủy",HttpStatus.OK);
    }

    @GetMapping("/get/details/{appointmentId}")
    public ResponseEntity<AppointmentDetails> getAppointmentDetailsWithName(@PathVariable Long appointmentId){
        return new ResponseEntity<>(appointmentService.getAppointmentDetailsWithName(appointmentId), HttpStatus.OK);

    }

    @GetMapping("/getAllByPatient/{patientId}")
    public ResponseEntity<List<AppointmentDetails>> getAllAppointmentsByPatientId(@PathVariable Long patientId) {
        return new ResponseEntity<>(appointmentService.getAllAppointmentDetailsByPatientId(patientId),
                HttpStatus.OK);
    }

    @GetMapping("/getAllByDoctor/{doctorId}")
    public ResponseEntity<List<AppointmentDetails>> getAllAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return new ResponseEntity<>(appointmentService.getAllAppointmentDetailsByDoctorId(doctorId),
                HttpStatus.OK);
    }

    @GetMapping("/countByPatient/{patientId}")
    public ResponseEntity<List<MonthlyVisitDTO>> getAppointmentCountByPatientId(@PathVariable Long patientId) {
        return new ResponseEntity<>(appointmentService.getAppointmentCountByPatient(patientId),
                HttpStatus.OK);
    }

    @GetMapping("/countByDoctor/{doctorId}")
    public ResponseEntity<List<MonthlyVisitDTO>> getAppointmentCountByDoctorId(@PathVariable Long doctorId) {
        return new ResponseEntity<>(appointmentService.getAppointmentCountByDoctor(doctorId),
                HttpStatus.OK);
    }

    @GetMapping("/countPatientsByDoctor/{doctorId}")
    public ResponseEntity<List<MonthlyVisitDTO>> getPatientCountByDoctorId(@PathVariable Long doctorId) {
        return new ResponseEntity<>(appointmentService.getPatientCountByDoctor(doctorId),
                HttpStatus.OK);
    }

    @GetMapping("/visitCount")
    public ResponseEntity<List<MonthlyVisitDTO>> getAppointmentCount() {
        return new ResponseEntity<>(appointmentService.getAppointmentCount(),
                HttpStatus.OK);
    }

    @GetMapping("/countReasonsByPatient/{patientId}")
    public ResponseEntity<List<ReasonCountDTO>> getReasonsByPatient(@PathVariable Long patientId) {
        return new ResponseEntity<>(appointmentService.getReasonCountByPatient(patientId),
                HttpStatus.OK);
    }

    @GetMapping("/countReasonsByDoctor/{doctorId}")
    public ResponseEntity<List<ReasonCountDTO>> getReasonsByDoctor(@PathVariable Long doctorId) {
        return new ResponseEntity<>(appointmentService.getReasonCountByDoctor(doctorId),
                HttpStatus.OK);
    }

    @GetMapping("/countReasons")
    public ResponseEntity<List<ReasonCountDTO>> getReasons() {
        return new ResponseEntity<>(appointmentService.getReasonCount(),
                HttpStatus.OK);
    }

    @GetMapping("/getMedicinesByPatient/{patientId}")
    public ResponseEntity<List<MedicineDTO>> getMedicinesByPatientId(@PathVariable Long patientId) {
        return new ResponseEntity<>(prescriptionService.getMedicineByPatientId(patientId),
                HttpStatus.OK);
    }

    @GetMapping("/today")
    public ResponseEntity<List<AppointmentDetails>> getTodaysAppointment() {
        return new ResponseEntity<>(appointmentService.getTodaysAppointment(),
                HttpStatus.OK);
    }
}
