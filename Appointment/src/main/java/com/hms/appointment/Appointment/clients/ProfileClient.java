package com.hms.appointment.Appointment.clients;

import com.hms.appointment.Appointment.dto.DoctorDTO;
import com.hms.appointment.Appointment.dto.DoctorName;
import com.hms.appointment.Appointment.dto.PatientDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "profile-api")
@ClientHeaderParam(name = "X-Secret-Key", value = "SECRET")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProfileClient {

    @GET
    @Path("/profile/doctor/exists/{id}")
    Boolean doctorExists(@PathParam("id") Long id);

    @GET
    @Path("/profile/patient/exists/{id}")
    Boolean patientExists(@PathParam("id") Long id);

    @GET
    @Path("/profile/patient/get/{id}")
    PatientDTO getPatientById(@PathParam("id") Long id);

    @GET
    @Path("/profile/doctor/get/{id}")
    DoctorDTO getDoctorById(@PathParam("id") Long id);

    @GET
    @Path("/profile/doctor/getDoctorsById")
    List<DoctorName> getDoctorsById(@QueryParam("ids") List<Long> ids);

    @GET
    @Path("/profile/patient/getPatientsById")
    List<DoctorName> getPatientsById(@QueryParam("ids") List<Long> ids);
}

