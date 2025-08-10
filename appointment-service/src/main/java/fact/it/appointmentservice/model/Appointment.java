package fact.it.appointmentservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appointmentNumber; // UUID-like number

    // References to other services
    private String patientId; // Mongo ObjectId from patient-service
    private Long doctorId;    // doctor-service ID
    private Long timeslotId;  // doctor-service TimeSlot ID

    private String reason;    // optional: "initial consult", etc.
}
