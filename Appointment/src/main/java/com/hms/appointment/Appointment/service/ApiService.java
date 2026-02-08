package com.hms.appointment.Appointment.service;


import com.hms.appointment.Appointment.dto.DoctorDTO;
import com.hms.appointment.Appointment.dto.PatientDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApiService {
    private final WebClient.Builder webClient;

    public Mono<Boolean> doctorExists(Long id) {

        return webClient.build().get()
                .uri("http://localhost:9100/profile/doctor/exists/"+id)
                .retrieve()
                .bodyToMono(Boolean.class);

    }

    public Mono<Boolean> patientExists(Long id) {

        return webClient.build().get()
                .uri("http://localhost:9100/profile/patient/exists/"+id)
                .retrieve()
                .bodyToMono(Boolean.class);

    }

    public Mono<PatientDTO> getPatientById(Long id) {
        return webClient.build().get()
                .uri("http://localhost:9100/profile/patient/get/" + id)
                .retrieve()
                .bodyToMono(PatientDTO.class);
    }

    public Mono<DoctorDTO> getDoctorById(Long id) {
        return webClient.build().get()
                .uri("http://localhost:9100/profile/doctor/get/" + id)
                .retrieve()
                .bodyToMono(DoctorDTO.class);
    }
}
