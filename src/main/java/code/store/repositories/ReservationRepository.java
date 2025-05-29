package code.store.repositories;

import code.store.entities.EventEntity;
import code.store.entities.ParticipantEntity;
import code.store.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    boolean existsByParticipantAndEvent(ParticipantEntity participant, EventEntity event);
    Optional<ReservationEntity> findByParticipantIdAndEventId(Long participantId, Long eventId);
    List<ReservationEntity> findByParticipantId(Long participantId);
}

