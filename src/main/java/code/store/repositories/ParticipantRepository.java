package code.store.repositories;

import code.store.entities.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity,Long> {
}
