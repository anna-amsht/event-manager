package code.api.dto;


import code.store.entities.InvitationStatus;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    InvitationStatus status;

    String eventTitle;
    String organizerName;
}
