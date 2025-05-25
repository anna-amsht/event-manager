package code.api.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto {

    Long id;
    @NonNull
    String title;
    @NonNull
    String description;

    @JsonProperty("number_of_seats")
    @Min(0)
    Integer numberOfSeats;


    @JsonProperty("date_time")
    String dateTime;
    @JsonProperty("format")
    String format;
    String location;

    Long organizerId;
}
