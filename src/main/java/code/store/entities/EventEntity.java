package code.store.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
    Long id;

    @Column(unique = true)
    String title;

    String description;
    @Min(0)
    @Column(nullable = false)
    Integer numberOfSeats;

    LocalDateTime dateTime;
    String location;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    OrganizerEntity organizer;

    @OneToMany(mappedBy = "event")
    private List<ParticipantEntity> participants = new ArrayList<>();

}
