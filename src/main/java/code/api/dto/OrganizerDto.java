package code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizerDto {
    @NonNull
    Long id;

    String password;

    @NonNull
    String username;

    boolean isRegistered;

}
