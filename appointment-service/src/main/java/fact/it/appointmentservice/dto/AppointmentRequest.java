package fact.it.appointmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequest {
    private String patientNationalId; // used to look up patient in patient-service
    private Long doctorId;
    private Long timeslotId;
    private String reason;
}
