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
import static org.mockito.ArgumentMatchers.*;
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
    void getDoctor_found_mapsResponse() {
        Doctor d = Doctor.builder()
                .id(1L).name("Dr. Alice").specialty("Cardiology").build();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(d));

        DoctorResponse r = doctorService.getDoctor(1L);

        assertNotNull(r);
        assertEquals(1L, r.getId());
        assertEquals("Dr. Alice", r.getName());
        verify(doctorRepository).findById(1L);
    }

    @Test
    void getDoctor_notFound_returnsNull() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(doctorService.getDoctor(99L));
    }

    @Test
    void getTimeSlotsByDoctor_returnsList() {
        TimeSlot s1 = TimeSlot.builder().id(10L)
                .startAt(LocalDateTime.now()).endAt(LocalDateTime.now().plusMinutes(30))
                .status(TimeSlotStatus.AVAILABLE).build();
        when(timeSlotRepository.findByDoctorId(1L)).thenReturn(List.of(s1));

        List<TimeSlotDto> out = doctorService.getTimeSlotsByDoctor(1L);

        assertEquals(1, out.size());
        assertEquals(10L, out.get(0).getId());
        verify(timeSlotRepository).findByDoctorId(1L);
    }

    @Test
    void getTimeSlotsByDoctorBetween_filtersByRange() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusHours(2);
        TimeSlot s = TimeSlot.builder().id(20L)
                .startAt(from.plusMinutes(30)).endAt(from.plusMinutes(60))
                .status(TimeSlotStatus.AVAILABLE).build();

        when(timeSlotRepository.findByDoctorIdAndStartAtBetween(1L, from, to))
                .thenReturn(List.of(s));

        List<TimeSlotDto> out = doctorService.getTimeSlotsByDoctorBetween(1L, from, to);

        assertEquals(1, out.size());
        assertEquals(20L, out.get(0).getId());
        verify(timeSlotRepository).findByDoctorIdAndStartAtBetween(1L, from, to);
    }

    @Test
    void reserveSlot_available_becomesReserved() {
        TimeSlot slot = TimeSlot.builder().id(5L)
                .status(TimeSlotStatus.AVAILABLE).build();
        when(timeSlotRepository.findById(5L)).thenReturn(Optional.of(slot));

        ReserveSlotResponse resp = doctorService.reserveSlot(5L);

        assertTrue(resp.isReserved());
        assertEquals("Time slot reserved successfully", resp.getMessage());
        assertEquals(TimeSlotStatus.RESERVED, slot.getStatus());
        verify(timeSlotRepository).save(slot);
    }

    @Test
    void reserveSlot_alreadyReserved_returnsMessage() {
        TimeSlot slot = TimeSlot.builder().id(6L)
                .status(TimeSlotStatus.RESERVED).build();
        when(timeSlotRepository.findById(6L)).thenReturn(Optional.of(slot));

        ReserveSlotResponse resp = doctorService.reserveSlot(6L);

        assertFalse(resp.isReserved());
        assertEquals("Time slot already reserved", resp.getMessage());
        verify(timeSlotRepository, never()).save(any());
    }

    @Test
    void reserveSlot_notFound() {
        when(timeSlotRepository.findById(404L)).thenReturn(Optional.empty());
        ReserveSlotResponse resp = doctorService.reserveSlot(404L);
        assertFalse(resp.isReserved());
        assertEquals("Time slot not found", resp.getMessage());
    }
}

