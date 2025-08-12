package fact.it.patientservice.service;

import fact.it.patientservice.dto.PatientRequest;
import fact.it.patientservice.dto.PatientResponse;
import fact.it.patientservice.model.Patient;
import fact.it.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public void createPatient(PatientRequest patientRequest) {
        Patient patient = Patient.builder()
                .nationalId(patientRequest.getNationalId())
                .firstName(patientRequest.getFirstName())
                .lastName(patientRequest.getLastName())
                .email(patientRequest.getEmail())
                .phone(patientRequest.getPhone())
                .build();

        patientRepository.save(patient);
    }

    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::mapToPatientResponse)
                .toList();
    }

    public PatientResponse getByNationalId(String nationalId) {
        return patientRepository.findByNationalId(nationalId)
                .map(this::mapToPatientResponse)
                .orElse(null);
    }

    public boolean updatePatient(String nationalId, PatientRequest req) {
        return patientRepository.findByNationalId(nationalId)
                .map(p -> {
                    p.setFirstName(req.getFirstName());
                    p.setLastName(req.getLastName());
                    p.setEmail(req.getEmail());
                    p.setPhone(req.getPhone());
                    patientRepository.save(p);
                    return true;
                })
                .orElse(false);
    }

    public boolean deleteByNationalId(String nationalId) {
        return patientRepository.deleteByNationalId(nationalId) > 0;
    }


    private PatientResponse mapToPatientResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .nationalId(patient.getNationalId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .build();
    }
}
