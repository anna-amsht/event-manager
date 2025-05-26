package code.store.repositories;

import code.store.entities.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity,Long> {
    Optional<ParticipantEntity> findByUsername(String username);
}
