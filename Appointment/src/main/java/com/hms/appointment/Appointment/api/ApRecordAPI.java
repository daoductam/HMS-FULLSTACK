package com.hms.appointment.Appointment.api;

import com.hms.appointment.Appointment.dto.ApRecordDTO;
import com.hms.appointment.Appointment.dto.MedicineDTO;
import com.hms.appointment.Appointment.dto.PrescriptionDetails;
import com.hms.appointment.Appointment.dto.RecordDetails;
import com.hms.appointment.Appointment.service.ApRecordService;
import com.hms.appointment.Appointment.service.MedicineService;
import com.hms.appointment.Appointment.service.PrescriptionService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path("/appointment/report")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ApRecordAPI {
    private final ApRecordService apRecordService;
    private final PrescriptionService prescriptionService;
    private final MedicineService medicineService;

    @POST
    @Path("/create")
    public Response createAppointmentReport(ApRecordDTO request) {
        return Response.status(Response.Status.CREATED)
                .entity(apRecordService.createApRecord(request))
                .build();
    }

    @PUT
    @Path("/update")
    public Response updateAppointmentReport(ApRecordDTO request) {
        apRecordService.updateApRecord(request);
        return Response.ok("Appointment Report Updated").build();
    }

    @GET
    @Path("/getDetailsByAppointmentId/{appointmentId}")
    public Response getAppointmentReportDetailsByAppointmentId(@PathParam("appointmentId") Long appointmentId) {
        return Response.ok(apRecordService.getApRecordDetailsByAppointmentId(appointmentId)).build();
    }

    @GET
    @Path("/getByAppointmentId/{appointmentId}")
    public Response getAppointmentReportByAppointmentId(@PathParam("appointmentId") Long appointmentId) {
        return Response.ok(apRecordService.getApRecordByAppointmentId(appointmentId)).build();
    }

    @GET
    @Path("/getById/{recordId}")
    public Response getAppointmentReportById(@PathParam("recordId") Long recordId) {
        return Response.ok(apRecordService.getApRecordById(recordId)).build();
    }

    @GET
    @Path("/getRecordsByPatientId/{patientId}")
    public Response getRecordsByPatientId(@PathParam("patientId") Long patientId) {
        return Response.ok(apRecordService.getRecordsByPatientId(patientId)).build();
    }

    @GET
    @Path("/isRecordExists/{appointmentId}")
    public Response isRecordExists(@PathParam("appointmentId") Long appointmentId) {
        return Response.ok(apRecordService.isRecordExists(appointmentId)).build();
    }

    @GET
    @Path("/getPrescriptionsByPatientId/{patientId}")
    public Response getPrescriptionsByPatientId(@PathParam("patientId") Long patientId) {
        return Response.ok(prescriptionService.getPrescriptionByPatientId(patientId)).build();
    }

    @GET
    @Path("/getAllPrescriptions")
    public Response getAllPrescriptions() {
        return Response.ok(prescriptionService.getPrescriptions()).build();
    }

    @GET
    @Path("/getMedicinesByPrescriptionId/{prescriptionId}")
    public Response getMedicinesByPrescriptionId(@PathParam("prescriptionId") Long prescriptionId) {
        return Response.ok(medicineService.getAllMedicinesByPrescriptionId(prescriptionId)).build();
    }
}

