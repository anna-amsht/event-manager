package code.api.dto;


import code.store.entities.EventEntity;
import code.store.entities.ParticipantEntity;
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
    Long eventId;
    @NonNull
    Long participantId;
}
