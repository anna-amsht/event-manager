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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(unique = true)
    String title;

    String description;
    @Min(5) // TODO: я не ебу почему эта шняга не работает Integer/int?
    @Column(nullable = false)
    Integer numberOfSeats;

    LocalDateTime dateTime;
    String location;

    String format;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    OrganizerEntity organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations = new ArrayList<>();
}
