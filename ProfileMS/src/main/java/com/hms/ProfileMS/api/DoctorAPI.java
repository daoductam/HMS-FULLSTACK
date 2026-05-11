package com.hms.ProfileMS.api;

import com.hms.ProfileMS.dto.DoctorDTO;
import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PageResponse;
import com.hms.ProfileMS.service.DoctorService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path("/profile/doctor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class DoctorAPI {

    private final DoctorService doctorService;

    @POST
    @Path("/add")
    public Response addUser(@Valid DoctorDTO doctorDTO)  {
        return Response.status(Response.Status.CREATED)
                .entity(doctorService.addDoctor(doctorDTO))
                .build();
    }

    @GET
    @Path("/get/{id}")
    public Response getDoctorById(@PathParam("id") Long id){
        return Response.ok(doctorService.getDoctorById(id)).build();
    }

    @GET
    @Path("/getProfileId/{id}")
    public Response getProfileId(@PathParam("id") Long id){
        return Response.ok(doctorService.getDoctorById(id).getProfilePictureId()).build();
    }

    @PUT
    @Path("/update")
    public Response updateDoctor(DoctorDTO doctorDTO){
        return Response.ok(doctorService.updateDoctor(doctorDTO)).build();
    }

    @GET
    @Path("/exists/{id}")
    public Response doctorExists(@PathParam("id") Long id){
        return Response.ok(doctorService.doctorExists(id)).build();
    }

    @GET
    @Path("/dropdowns")
    public Response getDoctorDropdowns() {
        return Response.ok(doctorService.getDoctorDropdowns()).build();
    }

    @GET
    @Path("/getAll")
    public Response getAllDoctors() {
        return Response.ok(doctorService.getAllDoctors()).build();
    }

    @GET
    @Path("/getAllPaginated")
    public Response getAllDoctorsPaginated(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return Response.ok(doctorService.getAllDoctorsPaginated(page, size)).build();
    }

    @GET
    @Path("/getDoctorsById")
    public Response getDoctorsById(@QueryParam("ids") List<Long> ids) {
        return Response.ok(doctorService.getDoctorsById(ids)).build();
    }
}
