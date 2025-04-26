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
@FieldDefaults(level = AccessLevel.PRIVATE) // примочки Lombock
@Entity
@Table(name="organization")
public class OrganizationSystemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // на уровне всей базы данных создает для таблиц уникальный id
    Long id;

    @Builder.Default
    @OneToMany
    @JoinColumn(name = "event_title")
    List<EventEntity> events = new ArrayList<>();

}
