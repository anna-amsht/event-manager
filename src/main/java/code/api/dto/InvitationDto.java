package code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvitationDto {
    @NonNull
    Long id;
    @NonNull
    Long eventId;
    @NonNull
    Long participantId;
    @NonNull
    Long organizerId;

}
