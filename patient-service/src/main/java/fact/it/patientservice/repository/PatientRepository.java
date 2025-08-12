package fact.it.patientservice.repository;

import fact.it.patientservice.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByNationalId(String nationalId);
    long deleteByNationalId(String nationalId);
}
