package fact.it.appointmentservice;

import fact.it.appointmentservice.dto.*;
import fact.it.appointmentservice.model.Appointment;
import fact.it.appointmentservice.repository.AppointmentRepository;
import fact.it.appointmentservice.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceUnitTests {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(appointmentService, "patientServiceBaseUrl", "localhost:8080");
        ReflectionTestUtils.setField(appointmentService, "doctorServiceBaseUrl", "localhost:8083");
    }

    @Test
    void placeAppointment_success() {
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientNationalId("BEL123");
        req.setDoctorId(1L);
        req.setTimeslotId(10L);
        req.setReason("Consult");

        PatientResponse patient = PatientResponse.builder().id("pat-1").nationalId("BEL123").build();
        ReserveSlotResponse reserve = ReserveSlotResponse.builder().reserved(true).message("ok").build();

        // GET patient
        when(webClient.get()
                .uri(anyString(), any(Function.class))
                .retrieve()
                .bodyToMono(eq(PatientResponse.class)))
                .thenReturn(Mono.just(patient));

        // POST reserve
        when(webClient.post()
                .uri(anyString())
                .bodyValue(any())
                .retrieve()
                .bodyToMono(eq(ReserveSlotResponse.class)))
                .thenReturn(Mono.just(reserve));

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        boolean result = appointmentService.placeAppointment(req);

        assertTrue(result);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void placeAppointment_fails_whenPatientNotFound() {
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientNationalId("UNKNOWN");
        req.setDoctorId(1L);
        req.setTimeslotId(10L);
        req.setReason("Consult");

        when(webClient.get()
                .uri(anyString(), any(Function.class))
                .retrieve()
                .bodyToMono(eq(PatientResponse.class)))
                .thenReturn(Mono.empty());

        boolean result = appointmentService.placeAppointment(req);

        assertFalse(result);
        verify(appointmentRepository, never()).save(any());
        verify(webClient, never()).post();
    }

    @Test
    void placeAppointment_fails_whenReserveRejected() {
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientNationalId("BEL123");
        req.setDoctorId(1L);
        req.setTimeslotId(10L);
        req.setReason("Consult");

        PatientResponse patient = PatientResponse.builder().id("pat-1").nationalId("BEL123").build();
        ReserveSlotResponse reserve = ReserveSlotResponse.builder().reserved(false).message("already").build();

        when(webClient.get()
                .uri(anyString(), any(Function.class))
                .retrieve()
                .bodyToMono(eq(PatientResponse.class)))
                .thenReturn(Mono.just(patient));

        when(webClient.post()
                .uri(anyString())
                .bodyValue(any())
                .retrieve()
                .bodyToMono(eq(ReserveSlotResponse.class)))
                .thenReturn(Mono.just(reserve));

        boolean result = appointmentService.placeAppointment(req);

        assertFalse(result);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void getAllAppointments_mapsList() {
        Appointment a1 = Appointment.builder()
                .appointmentNumber("A1").patientId("P1").doctorId(1L).timeslotId(10L).reason("R1").build();
        Appointment a2 = Appointment.builder()
                .appointmentNumber("A2").patientId("P2").doctorId(2L).timeslotId(20L).reason("R2").build();

        when(appointmentRepository.findAll()).thenReturn(List.of(a1, a2));

        var out = appointmentService.getAllAppointments();

        assertEquals(2, out.size());
        assertEquals("A1", out.get(0).getAppointmentNumber());
        assertEquals("A2", out.get(1).getAppointmentNumber());
        verify(appointmentRepository).findAll();
    }
}
