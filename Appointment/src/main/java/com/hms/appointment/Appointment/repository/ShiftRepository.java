package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.entity.Shift;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class ShiftRepository implements PanacheRepository<Shift> {
    
    public Optional<Shift> findByName(String name) {
        return find("name", name).firstResultOptional();
    }
}
