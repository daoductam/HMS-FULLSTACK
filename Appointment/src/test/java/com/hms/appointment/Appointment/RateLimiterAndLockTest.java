package com.hms.appointment.Appointment;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.redis.client.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import jakarta.inject.Inject;
import com.hms.appointment.Appointment.service.AppointmentService;
import com.hms.appointment.Appointment.dto.AppointmentDTO;
import java.time.LocalDateTime;

@QuarkusTest
public class RateLimiterAndLockTest {

    @InjectMock
    RedisDataSource redisDataSource;

    @Inject
    AppointmentService appointmentService;

    @Test
    public void testDistributedLockAcquisitionFailureThrowsException() {
        // Mock RedisDataSource to return null when SET command is executed (meaning lock acquisition fails)
        Mockito.when(redisDataSource.execute(
                Mockito.eq("SET"),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.eq("NX"),
                Mockito.eq("PX"),
                Mockito.anyString()
        )).thenReturn(null); // return null to simulate lock acquisition failure

        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentTime(LocalDateTime.now().plusDays(1));

        // When invoking scheduleAppointment, it should throw exception due to lock failure
        Assertions.assertThrows(RuntimeException.class, () -> {
            appointmentService.scheduleAppointment(dto);
        });
    }

    @Test
    public void testDistributedLockAcquisitionSuccess() {
        // Mock RedisDataSource to return "OK" when SET command is executed (meaning lock acquisition succeeds)
        Response okResponse = Mockito.mock(Response.class);
        Mockito.when(okResponse.toString()).thenReturn("OK");

        Mockito.when(redisDataSource.execute(
                Mockito.eq("SET"),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.eq("NX"),
                Mockito.eq("PX"),
                Mockito.anyString()
        )).thenReturn(okResponse);

        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(9999L); // Use dummy ID
        dto.setPatientId(1L);
        dto.setAppointmentTime(LocalDateTime.now().plusDays(1));

        // Try to schedule. It will validate doctor profile via RestClient (which might fail if doctor doesn't exist,
        // but it will pass the Lock interceptor phase first).
        try {
            appointmentService.scheduleAppointment(dto);
        } catch (Exception e) {
            // Verify that the exception thrown is NOT about lock acquisition, but about the business logic (e.g. Doctor profile client)
            Assertions.assertFalse(e.getMessage().contains("Could not acquire lock"));
        }
    }
}
