package code.api.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto {
    @NonNull
    Long id;

    @NonNull
    String title;

    @NonNull
    String description;

    @NonNull
    Integer numberOfSeats;

    @NonNull
    LocalDateTime dateTime;

    @NonNull
    String location;

    @NonNull
    Long organizerId;

}
