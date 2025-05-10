package code.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationDto {
    @NonNull
    Long id;

    @NonNull
    Long participantId;

    @NonNull
    Long eventId;
}
