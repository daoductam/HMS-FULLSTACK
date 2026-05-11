package com.hms.appointment.Appointment.api;

import com.hms.appointment.Appointment.dto.*;
import com.hms.appointment.Appointment.service.ScheduleService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Path("/appointment/doctor-schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ScheduleAPI {
    private final ScheduleService scheduleService;

    @POST
    @Path("/create")
    public Response createSchedule(CreateScheduleRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(scheduleService.createSchedule(request))
                .build();
    }

    @POST
    @Path("/lock")
    public Response lockSchedule(LockScheduleRequest request) {
        return Response.ok(scheduleService.lockSchedule(request)).build();
    }

    @PUT
    @Path("/unlock/{doctorId}")
    public Response unlockSchedule(
            @PathParam("doctorId") Long doctorId,
            @QueryParam("scheduleDate") LocalDate scheduleDate) {
        return Response.ok(scheduleService.unlockSchedule(doctorId, scheduleDate)).build();
    }

    @GET
    @Path("/get/{doctorId}")
    public Response getSchedule(
            @PathParam("doctorId") Long doctorId,
            @QueryParam("scheduleDate") LocalDate scheduleDate) {
        return Response.ok(scheduleService.getSchedule(doctorId, scheduleDate)).build();
    }

    @GET
    @Path("/getAll/{doctorId}")
    public Response getSchedulesByDoctor(
            @PathParam("doctorId") Long doctorId,
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate) {
        // Nếu không có startDate/endDate, lấy 30 ngày từ hôm nay
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = startDate.plusDays(30);
        }
        return Response.ok(scheduleService.getSchedulesByDoctor(doctorId, startDate, endDate)).build();
    }

    @GET
    @Path("/shifts")
    public Response getAllShifts() {
        return Response.ok(scheduleService.getAllShifts()).build();
    }

    @POST
    @Path("/initialize-shifts")
    public Response initializeShifts() {
        scheduleService.initializeDefaultShifts();
        return Response.ok("Đã khởi tạo ca làm việc mặc định").build();
    }
}



