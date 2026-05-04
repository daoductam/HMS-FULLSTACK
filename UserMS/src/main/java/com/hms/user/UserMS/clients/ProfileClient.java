package com.hms.user.UserMS.clients;

import com.hms.user.UserMS.dto.UserDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "profile-api")
@ClientHeaderParam(name = "X-Secret-Key", value = "SECRET")
public interface ProfileClient {

    @POST
    @Path("/profile/doctor/add")
    Long addDoctor(UserDTO userDTO);

    @POST
    @Path("/profile/patient/add")
    Long addPatient(UserDTO userDTO);

    @GET
    @Path("/profile/doctor/getProfileId/{id}")
    Long getDoctor(@PathParam("id") Long id);

    @GET
    @Path("/profile/patient/getProfileId/{id}")
    Long getPatient(@PathParam("id") Long id);
}

