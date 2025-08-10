package fact.it.appointmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {
    private String appointmentNumber;
    private String patientId;
    private Long doctorId;
    private Long timeslotId;
    private String reason;
}
