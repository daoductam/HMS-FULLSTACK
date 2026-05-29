package com.hms.appointment.Appointment.api;

import com.hms.appointment.Appointment.dto.*;
import com.hms.appointment.Appointment.service.AppointmentService;
import com.hms.appointment.Appointment.service.PrescriptionService;
import com.hms.hms_common.limiter.RateLimit;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path("/appointment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AppointmentAPI {

    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;

    @POST
    @Path("/schedule")
    @RateLimit(limit = 3, duration = 60)
    public Response scheduleAppointment(AppointmentDTO appointmentDTO)  {
        return Response.status(Response.Status.CREATED)
                .entity(appointmentService.scheduleAppointment(appointmentDTO))
                .build();
    }

    @GET
    @Path("/get/{appointmentId}")
    public Response getAppointmentDetails(@PathParam("appointmentId") Long appointmentId){
        return Response.ok(appointmentService.getAppointmentDetails(appointmentId)).build();
    }

    @PUT
    @Path("/cancel/{appointmentId}")
    public Response cancelAppointment(@PathParam("appointmentId") Long appointmentId){
        appointmentService.cancelAppointment(appointmentId);
        return Response.ok("Cuộc hẹn đã hủy").build();
    }

    @GET
    @Path("/get/details/{appointmentId}")
    public Response getAppointmentDetailsWithName(@PathParam("appointmentId") Long appointmentId){
        return Response.ok(appointmentService.getAppointmentDetailsWithName(appointmentId)).build();
    }

    @GET
    @Path("/getAllByPatient/{patientId}")
    public Response getAllAppointmentsByPatientId(@PathParam("patientId") Long patientId) {
        return Response.ok(appointmentService.getAllAppointmentDetailsByPatientId(patientId)).build();
    }

    @GET
    @Path("/getAllByDoctor/{doctorId}")
    public Response getAllAppointmentsByDoctorId(@PathParam("doctorId") Long doctorId) {
        return Response.ok(appointmentService.getAllAppointmentDetailsByDoctorId(doctorId)).build();
    }

    @GET
    @Path("/countByPatient/{patientId}")
    public Response getAppointmentCountByPatientId(@PathParam("patientId") Long patientId) {
        return Response.ok(appointmentService.getAppointmentCountByPatient(patientId)).build();
    }

    @GET
    @Path("/countByDoctor/{doctorId}")
    public Response getAppointmentCountByDoctorId(@PathParam("doctorId") Long doctorId) {
        return Response.ok(appointmentService.getAppointmentCountByDoctor(doctorId)).build();
    }

    @GET
    @Path("/countPatientsByDoctor/{doctorId}")
    public Response getPatientCountByDoctorId(@PathParam("doctorId") Long doctorId) {
        return Response.ok(appointmentService.getPatientCountByDoctor(doctorId)).build();
    }

    @GET
    @Path("/visitCount")
    public Response getAppointmentCount() {
        return Response.ok(appointmentService.getAppointmentCount()).build();
    }

    @GET
    @Path("/countReasonsByPatient/{patientId}")
    public Response getReasonsByPatient(@PathParam("patientId") Long patientId) {
        return Response.ok(appointmentService.getReasonCountByPatient(patientId)).build();
    }

    @GET
    @Path("/countReasonsByDoctor/{doctorId}")
    public Response getReasonsByDoctor(@PathParam("doctorId") Long doctorId) {
        return Response.ok(appointmentService.getReasonCountByDoctor(doctorId)).build();
    }

    @GET
    @Path("/countReasons")
    public Response getReasons() {
        return Response.ok(appointmentService.getReasonCount()).build();
    }

    @GET
    @Path("/getMedicinesByPatient/{patientId}")
    public Response getMedicinesByPatientId(@PathParam("patientId") Long patientId) {
        return Response.ok(prescriptionService.getMedicineByPatientId(patientId)).build();
    }

    @GET
    @Path("/today")
    public Response getTodaysAppointment() {
        return Response.ok(appointmentService.getTodaysAppointment()).build();
    }
}
