package fact.it.appointmentservice.service;

import fact.it.appointmentservice.dto.*;
import fact.it.appointmentservice.model.Appointment;
import fact.it.appointmentservice.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final WebClient webClient;

    @Value("${patientservice.baseurl}")
    private String patientServiceBaseUrl;

    @Value("${doctorservice.baseurl}")
    private String doctorServiceBaseUrl;

    public boolean placeAppointment(AppointmentRequest request) {
        // 1) Fetch patient by nationalId from patient-service (assumes patient-service on 8080)
        PatientResponse patient = webClient.get()
                .uri("http://" + patientServiceBaseUrl + "/api/patient",
                        uriBuilder -> uriBuilder.queryParam("nationalId", request.getPatientNationalId()).build())
                .retrieve()
                .bodyToMono(PatientResponse.class)
                .block();

        if (patient == null || patient.getId() == null) {
            return false; // patient not found
        }

        ReserveSlotResponse reserve = webClient.post()
                .uri("http://" + doctorServiceBaseUrl + "/api/timeslot/reserve")
                .bodyValue(new ReserveSlotRequest(request.getTimeslotId()))
                .retrieve()
                .bodyToMono(ReserveSlotResponse.class)
                .block();

        if (reserve == null || !reserve.isReserved()) {
            return false; // reservation failed or already reserved
        }

        Appointment appt = Appointment.builder()
                .appointmentNumber(UUID.randomUUID().toString())
                .patientId(patient.getId())
                .doctorId(request.getDoctorId())
                .timeslotId(request.getTimeslotId())
                .reason(request.getReason())
                .build();

        appointmentRepository.save(appt);
        return true;
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        return new AppointmentResponse(
                a.getAppointmentNumber(),
                a.getPatientId(),
                a.getDoctorId(),
                a.getTimeslotId(),
                a.getReason()
        );
    }
}
