package code.store.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="participants")
public class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    boolean isRegistered = false;

    String username;
    String password;


    @OneToMany(mappedBy = "participant")
    List<ReservationEntity> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "participant")
    List<InvitationEntity> invitations;

}
