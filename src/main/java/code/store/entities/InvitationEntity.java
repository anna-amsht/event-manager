package code.store.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "invitations")
public class InvitationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;


}
