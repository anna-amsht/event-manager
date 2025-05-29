package code.api.services;

import code.api.exceptions.NotFoundException;
import code.store.entities.*;
import code.store.repositories.InvitationRepository;
import code.store.repositories.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final InvitationRepository invitationRepository;


    public ParticipantEntity registerParticipant(ParticipantEntity participantEntity) {
        participantEntity.setRegistered(true);
        return participantRepository.save(participantEntity);
    }

    public List<EventEntity> getEventsForParticipant(Long participantId) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Участник не найден"));

        List<EventEntity> events = new ArrayList<>();
        for (ReservationEntity reservation : participant.getReservations()) {
            events.add(reservation.getEvent());
        }
        return events;
    }

    public List<InvitationEntity> getInvitationsByParticipantId(Long participantId) {
        return invitationRepository.findByParticipantId(participantId)
                .orElseThrow(() -> new NotFoundException("Participant not found"));
    }
}
