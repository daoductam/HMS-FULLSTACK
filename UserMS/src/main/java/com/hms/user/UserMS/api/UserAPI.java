package com.hms.user.UserMS.api;

import com.hms.user.UserMS.dto.*;
import com.hms.user.UserMS.exception.ErrorCode;
import com.hms.user.UserMS.exception.HmsException;
import com.hms.user.UserMS.jwt.CustomerUserDetails;
import com.hms.user.UserMS.jwt.JwtUtil;
import com.hms.user.UserMS.jwt.MyUserDetailsService;
import com.hms.user.UserMS.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserAPI {

    private final UserService userService;
    private final MyUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @POST
    @Path("/register")
    public Response registerUser(@Valid UserDTO userDTO) throws HmsException {
        userService.registerUser(userDTO);
        return Response.status(Response.Status.CREATED).entity(new ResponseDTO("Account created")).build();
    }

    @POST
    @Path("/login")
    public Response loginUser(LoginDTO loginDTO) throws HmsException {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(loginDTO.getEmail());
        userDTO.setPassword(loginDTO.getPassword());

        // Login check is performed inside userService.loginUser
        userService.loginUser(userDTO);

        final CustomerUserDetails userDetails = userDetailsService.loadUserByEmail(loginDTO.getEmail());
        if (userDetails == null) {
            throw new HmsException(ErrorCode.EMAIL_NOT_FOUND);
        }
        final String jwt = jwtUtil.generateToken(userDetails);
        return Response.ok(jwt).build();
    }

    @GET
    @Path("/getProfile/{id}")
    public Response getProfile(@PathParam("id") Long id) {
        return Response.ok(userService.getProfile(id)).build();
    }

    @GET
    @Path("/getRegistrationCounts")
    public Response getMonthlyRegistrationCounts() {
        return Response.ok(userService.getMonthlyRegistrationCounts()).build();
    }

    @PUT
    @Path("/admin/approve/{id}")
    @RolesAllowed("ADMIN")
    public Response approveDoctor(@PathParam("id") Long id) {
        userService.updateUserStatus(id, UserStatus.ACTIVE);
        return Response.ok("Doctor approved successfully").build();
    }

    @PUT
    @Path("/admin/reject/{id}")
    public Response rejectDoctor(@PathParam("id") Long id) {
        userService.updateUserStatus(id, UserStatus.REJECTED);
        return Response.ok("Doctor rejected").build();
    }

    @GET
    @Path("/getPendingDoctors")
    public Response getPendingDoctors() {
        return Response.ok(userService.getPendingDoctors()).build();
    }
}

