package fact.it.doctorservice.controller;

import fact.it.doctorservice.dto.ReserveSlotRequest;
import fact.it.doctorservice.dto.ReserveSlotResponse;
import fact.it.doctorservice.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timeslot")
@RequiredArgsConstructor
public class TimeSlotController {

    private final DoctorService doctorService;

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.OK)
    public ReserveSlotResponse reserve(@RequestBody ReserveSlotRequest request) {
        return doctorService.reserveSlot(request.getTimeslotId());
    }
}