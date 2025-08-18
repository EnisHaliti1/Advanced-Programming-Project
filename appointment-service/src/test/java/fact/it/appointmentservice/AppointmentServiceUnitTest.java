package fact.it.appointmentservice;

import fact.it.appointmentservice.dto.*;
import fact.it.appointmentservice.model.Appointment;
import fact.it.appointmentservice.repository.AppointmentRepository;
import fact.it.appointmentservice.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private WebClient webClient;

    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(appointmentService, "patientServiceBaseUrl", "localhost:8080");
        ReflectionTestUtils.setField(appointmentService, "doctorServiceBaseUrl", "localhost:8083");
    }

    @Test
    void testPlaceAppointment_Success() {
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientNationalId("BEL123");
        req.setDoctorId(1L);
        req.setTimeslotId(10L);
        req.setReason("Consult");

        PatientResponse patient = PatientResponse.builder().id("pat-1").nationalId("BEL123").build();
        ReserveSlotResponse reserveOk = ReserveSlotResponse.builder().reserved(true).message("ok").build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(PatientResponse.class))).thenReturn(Mono.just(patient));

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(ReserveSlotResponse.class))).thenReturn(Mono.just(reserveOk));

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        boolean result = appointmentService.placeAppointment(req);

        assertTrue(result);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testPlaceAppointment_FailureWhenPatientNotFound() {
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientNationalId("UNKNOWN");
        req.setDoctorId(1L);
        req.setTimeslotId(10L);
        req.setReason("Consult");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(PatientResponse.class))).thenReturn(Mono.empty());

        boolean result = appointmentService.placeAppointment(req);

        assertFalse(result);
        verify(appointmentRepository, never()).save(any());
        verify(webClient, never()).post(); // we never try to reserve if patient missing
    }

    @Test
    void testPlaceAppointment_FailureWhenReserveRejected() {
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientNationalId("BEL123");
        req.setDoctorId(1L);
        req.setTimeslotId(10L);
        req.setReason("Consult");

        PatientResponse patient = PatientResponse.builder().id("pat-1").nationalId("BEL123").build();
        ReserveSlotResponse reserveNo = ReserveSlotResponse.builder().reserved(false).message("already reserved").build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(PatientResponse.class))).thenReturn(Mono.just(patient));

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(ReserveSlotResponse.class))).thenReturn(Mono.just(reserveNo));

        boolean result = appointmentService.placeAppointment(req);

        assertFalse(result);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void testGetAllAppointments() {
        Appointment a1 = Appointment.builder()
                .appointmentNumber("A1").patientId("P1").doctorId(1L).timeslotId(10L).reason("R1").build();
        Appointment a2 = Appointment.builder()
                .appointmentNumber("A2").patientId("P2").doctorId(2L).timeslotId(20L).reason("R2").build();

        when(appointmentRepository.findAll()).thenReturn(List.of(a1, a2));

        var out = appointmentService.getAllAppointments();

        assertEquals(2, out.size());
        assertEquals("A1", out.get(0).getAppointmentNumber());
        assertEquals("A2", out.get(1).getAppointmentNumber());
        verify(appointmentRepository, times(1)).findAll();
    }
}
