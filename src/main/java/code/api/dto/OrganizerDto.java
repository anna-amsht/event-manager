package code.api.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizerDto {
    @NonNull
    Long id;
    @NonNull
    String username;
    @NonNull
    String password;
}
