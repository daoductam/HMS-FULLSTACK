package com.hms.ProfileMS.api;

import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PageResponse;
import com.hms.ProfileMS.dto.PatientDTO;
import com.hms.ProfileMS.service.PatientService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path("/profile/patient")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class PatientAPI {

    private final PatientService patientService;

    @POST
    @Path("/add")
    public Response addUser(PatientDTO patientDTO)  {
        return Response.status(Response.Status.CREATED)
                .entity(patientService.addPatient(patientDTO))
                .build();
    }

    @GET
    @Path("/get/{id}")
    public Response getPatientById(@PathParam("id") Long id){
        return Response.ok(patientService.getPatientById(id)).build();
    }

    @GET
    @Path("/getProfileId/{id}")
    public Response getProfileId(@PathParam("id") Long id){
        return Response.ok(patientService.getPatientById(id).getProfilePictureId()).build();
    }

    @PUT
    @Path("/update")
    public Response updatePatient(PatientDTO patientDTO){
        return Response.ok(patientService.updatePatient(patientDTO)).build();
    }

    @GET
    @Path("/exists/{id}")
    public Response patientExists(@PathParam("id") Long id){
        return Response.ok(patientService.patientExists(id)).build();
    }

    @GET
    @Path("/getAll")
    public Response getAllPatients() {
        return Response.ok(patientService.getAllPatients()).build();
    }

    @GET
    @Path("/getAllPaginated")
    public Response getAllPatientsPaginated(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return Response.ok(patientService.getAllPatientsPaginated(page, size)).build();
    }

    @GET
    @Path("/getPatientsById")
    public Response getPatientsById(@QueryParam("ids") List<Long> ids) {
        return Response.ok(patientService.getPatientsById(ids)).build();
    }

    @POST
    @Path("/listByIds")
    public Response getPatientsByIds(List<Long> ids) {
        List<PatientDTO> patients = patientService.findAllByIds(ids);
        return Response.ok(patients).build();
    }
}
