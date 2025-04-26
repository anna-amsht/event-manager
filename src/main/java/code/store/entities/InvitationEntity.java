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

    @ManyToOne
    @JoinColumn(name = "event_id")
    EventEntity event;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    ParticipantEntity participant;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    OrganizerEntity organizer;

    @Enumerated(EnumType.STRING)
    InvitationStatus status;

}
