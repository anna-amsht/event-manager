package code.store.repositories;

import code.store.entities.InvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<InvitationEntity, Long> {
    Optional<List<InvitationEntity>> findByParticipantId(Long participantId);
}
