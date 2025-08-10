package fact.it.patientservice.controller;

import fact.it.patientservice.dto.PatientRequest;
import fact.it.patientservice.dto.PatientResponse;
import fact.it.patientservice.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createPatient(@RequestBody PatientRequest patientRequest) {
        patientService.createPatient(patientRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PatientResponse getByNationalId(@RequestParam String nationalId) {
        return patientService.getByNationalId(nationalId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<PatientResponse> getAllPatients() {
        return patientService.getAllPatients();
    }
}
