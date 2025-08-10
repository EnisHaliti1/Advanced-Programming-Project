package fact.it.doctorservice.controller;

import fact.it.doctorservice.dto.DoctorResponse;
import fact.it.doctorservice.dto.TimeSlotDto;
import fact.it.doctorservice.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorResponse getDoctor(@PathVariable Long id) {
        return doctorService.getDoctor(id);
    }

    // /api/doctor/{id}/timeslots
    // Optional filters: ?from=2025-08-12T09:00:00&to=2025-08-12T17:00:00
    @GetMapping("/{id}/timeslots")
    @ResponseStatus(HttpStatus.OK)
    public List<TimeSlotDto> getTimeSlots(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        if (from != null && to != null) {
            return doctorService.getTimeSlotsByDoctorBetween(
                    id,
                    LocalDateTime.parse(from),
                    LocalDateTime.parse(to)
            );
        }
        return doctorService.getTimeSlotsByDoctor(id);
    }
}