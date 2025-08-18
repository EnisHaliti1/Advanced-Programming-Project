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
    void testGetDoctor_NotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(doctorService.getDoctor(99L));
        verify(doctorRepository, times(1)).findById(99L);
    }

}


