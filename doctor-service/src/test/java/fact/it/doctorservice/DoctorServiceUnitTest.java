package fact.it.doctorservice;

import fact.it.doctorservice.dto.DoctorResponse;
import fact.it.doctorservice.dto.ReserveSlotResponse;
import fact.it.doctorservice.dto.TimeSlotDto;
import fact.it.doctorservice.model.Doctor;
import fact.it.doctorservice.model.TimeSlot;
import fact.it.doctorservice.model.TimeSlotStatus;
import fact.it.doctorservice.repository.DoctorRepository;
import fact.it.doctorservice.repository.TimeSlotRepository;
import fact.it.doctorservice.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceUnitTest {

    @InjectMocks
    private DoctorService doctorService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Test
    void testGetDoctor_Found() {
        Doctor d = Doctor.builder()
                .id(1L).name("Dr. Alice Jensen").specialty("Cardiology").build();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(d));

        DoctorResponse resp = doctorService.getDoctor(1L);

        assertNotNull(resp);
        assertEquals(1L, resp.getId());
        assertEquals("Dr. Alice Jensen", resp.getName());
        assertEquals("Cardiology", resp.getSpecialty());
        verify(doctorRepository, times(1)).findById(1L);
    }
}


