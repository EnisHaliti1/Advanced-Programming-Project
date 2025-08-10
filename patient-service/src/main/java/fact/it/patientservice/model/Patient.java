package fact.it.patientservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "patient")
public class Patient {
    private String id;
    private String nationalId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
