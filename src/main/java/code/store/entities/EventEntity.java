package code.store.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="events")
public class EventEntity {
    @Id
    private Long id;

    @Column(unique = true)
    String title;
    String description;

    @Column(nullable = false)
    int numberOfSeats; // TODO: int / Integer?

    LocalDateTime dateTime;
    String location;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    ParticipantEntity organizer;

    @Builder.Default
    @OneToMany
    @JoinColumn(name = "user_id")
    List<ParticipantEntity> participants = new ArrayList<>();
}
