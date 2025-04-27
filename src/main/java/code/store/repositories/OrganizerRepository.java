package code.store.repositories;

import code.store.entities.OrganizerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerRepository extends JpaRepository<OrganizerEntity, Long> {
}
