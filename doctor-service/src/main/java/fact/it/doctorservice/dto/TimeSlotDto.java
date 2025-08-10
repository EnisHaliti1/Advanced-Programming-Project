package fact.it.doctorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotDto {
    private Long id;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String status; // AVAILABLE | RESERVED
    private Long doctorId;
}