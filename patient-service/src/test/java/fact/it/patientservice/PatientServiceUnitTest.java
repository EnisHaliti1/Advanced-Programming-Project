package fact.it.patientservice;

import fact.it.patientservice.dto.PatientRequest;
import fact.it.patientservice.dto.PatientResponse;
import fact.it.patientservice.model.Patient;
import fact.it.patientservice.repository.PatientRepository;
import fact.it.patientservice.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceUnitTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @Test
    void testCreatePatient() {
        PatientRequest req = new PatientRequest("BEL123", "Alice", "Jensen", "alice@example.com", "+3212345678");

        patientService.createPatient(req);

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    // all patients
    @Test
    void testGetAllPatients() {
        Patient p = Patient.builder()
                .id("id-1")
                .nationalId("BEL123")
                .firstName("Alice")
                .lastName("Jensen")
                .email("alice@example.com")
                .phone("+3212345678")
                .build();
        when(patientRepository.findAll()).thenReturn(List.of(p));

        List<PatientResponse> out = patientService.getAllPatients();

        assertEquals(1, out.size());
        PatientResponse r = out.get(0);
        assertEquals("id-1", r.getId());
        assertEquals("BEL123", r.getNationalId());
        assertEquals("Alice", r.getFirstName());
        assertEquals("Jensen", r.getLastName());
        assertEquals("alice@example.com", r.getEmail());
        assertEquals("+3212345678", r.getPhone());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void testGetByNationalId_Found() {
        Patient p = Patient.builder()
                .id("id-1")
                .nationalId("BEL123")
                .firstName("Alice")
                .lastName("Jensen")
                .email("alice@example.com")
                .phone("+3212345678")
                .build();
        when(patientRepository.findByNationalId("BEL123")).thenReturn(Optional.of(p));

        PatientResponse r = patientService.getByNationalId("BEL123");

        assertNotNull(r);
        assertEquals("id-1", r.getId());
        assertEquals("BEL123", r.getNationalId());
        verify(patientRepository, times(1)).findByNationalId("BEL123");
    }

    @Test
    void testGetByNationalId_NotFound() {
        when(patientRepository.findByNationalId("NOPE")).thenReturn(Optional.empty());
        assertNull(patientService.getByNationalId("NOPE"));
    }

    @Test
    void testUpdatePatient_Found() {
        Patient existing = Patient.builder()
                .id("id-1").nationalId("BEL123")
                .firstName("Old").lastName("Name").email("old@x").phone("1")
                .build();
        when(patientRepository.findByNationalId("BEL123")).thenReturn(Optional.of(existing));

        PatientRequest req = new PatientRequest("BEL123", "NewFirst", "NewLast", "new@x", "+320000");

        boolean ok = patientService.updatePatient("BEL123", req);

        assertTrue(ok);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void testUpdatePatient_NotFound() {
        when(patientRepository.findByNationalId("NOPE")).thenReturn(Optional.empty());
        boolean ok = patientService.updatePatient("NOPE",
                new PatientRequest("NOPE", "A", "B", "a@b", "123"));
        assertFalse(ok);
        verify(patientRepository, never()).save(any());
    }

    @Test
    void testDeleteByNationalId() {
        when(patientRepository.deleteByNationalId("BEL123")).thenReturn(1L);
        when(patientRepository.deleteByNationalId("NOPE")).thenReturn(0L);

        assertTrue(patientService.deleteByNationalId("BEL123"));
        assertFalse(patientService.deleteByNationalId("NOPE"));
    }
}

