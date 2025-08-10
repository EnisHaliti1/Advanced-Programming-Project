package fact.it.doctorservice.service;

import fact.it.doctorservice.dto.DoctorResponse;
import fact.it.doctorservice.dto.ReserveSlotResponse;
import fact.it.doctorservice.dto.TimeSlotDto;
import fact.it.doctorservice.model.Doctor;
import fact.it.doctorservice.model.TimeSlot;
import fact.it.doctorservice.model.TimeSlotStatus;
import fact.it.doctorservice.repository.DoctorRepository;
import fact.it.doctorservice.repository.TimeSlotRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;

    @PostConstruct
    public void loadData() {
        if (doctorRepository.count() == 0) {
            // Seed one doctor with a few AVAILABLE time slots
            Doctor doc = Doctor.builder()
                    .name("Dr. Alice Jensen")
                    .specialty("Cardiology")
                    .build();
            Doctor saved = doctorRepository.save(doc);

            LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
            TimeSlot s1 = TimeSlot.builder()
                    .doctor(saved)
                    .startAt(base)
                    .endAt(base.plusMinutes(30))
                    .status(TimeSlotStatus.AVAILABLE)
                    .build();
            TimeSlot s2 = TimeSlot.builder()
                    .doctor(saved)
                    .startAt(base.plusMinutes(30))
                    .endAt(base.plusMinutes(60))
                    .status(TimeSlotStatus.AVAILABLE)
                    .build();
            TimeSlot s3 = TimeSlot.builder()
                    .doctor(saved)
                    .startAt(base.plusHours(1))
                    .endAt(base.plusHours(1).plusMinutes(30))
                    .status(TimeSlotStatus.AVAILABLE)
                    .build();

            timeSlotRepository.saveAll(List.of(s1, s2, s3));
        }
    }

    public DoctorResponse getDoctor(Long id) {
        return doctorRepository.findById(id)
                .map(this::mapToDoctorResponse)
                .orElse(null);
    }

    public List<TimeSlotDto> getTimeSlotsByDoctor(Long doctorId) {
        return timeSlotRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToTimeSlotDto)
                .toList();
    }

    public List<TimeSlotDto> getTimeSlotsByDoctorBetween(Long doctorId, LocalDateTime from, LocalDateTime to) {
        return timeSlotRepository.findByDoctorIdAndStartAtBetween(doctorId, from, to).stream()
                .map(this::mapToTimeSlotDto)
                .toList();
    }

    @Transactional
    public  ReserveSlotResponse reserveSlot(Long timeslotId) {
        return timeSlotRepository.findById(timeslotId)
                .map(slot -> {
                    if (slot.getStatus() == TimeSlotStatus.RESERVED) {
                        return ReserveSlotResponse.builder()
                                .reserved(false)
                                .message("Time slot already reserved")
                                .build();
                    }
                    slot.setStatus(TimeSlotStatus.RESERVED);
                    timeSlotRepository.save(slot);
                    return ReserveSlotResponse.builder()
                            .reserved(true)
                            .message("Time slot reserved successfully")
                            .build();
                })
                .orElse(ReserveSlotResponse.builder()
                        .reserved(false)
                        .message("Time slot not found")
                        .build());
    }

    private DoctorResponse mapToDoctorResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialty(doctor.getSpecialty())
                .build();
    }

    private TimeSlotDto mapToTimeSlotDto(TimeSlot slot) {
        return TimeSlotDto.builder()
                .id(slot.getId())
                .startAt(slot.getStartAt())
                .endAt(slot.getEndAt())
                .status(slot.getStatus().name())
                .doctorId(slot.getDoctor() != null ? slot.getDoctor().getId() : null)
                .build();
    }
}
