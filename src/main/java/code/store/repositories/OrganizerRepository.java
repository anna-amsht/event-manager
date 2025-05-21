package code.store.repositories;

import code.store.entities.OrganizerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizerRepository extends JpaRepository<OrganizerEntity, Long> {
    Optional<OrganizerEntity> findByUsername(String username);
}
