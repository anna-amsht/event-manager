package code.store.repositories;

import code.store.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    Optional<EventEntity> findByTitle (String title);

    List<EventEntity> findAllByOrganizerId(Long organizerId);

}
