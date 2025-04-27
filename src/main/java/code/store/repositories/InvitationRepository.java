package code.store.repositories;

import code.store.entities.InvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<InvitationEntity, Long> {
}
