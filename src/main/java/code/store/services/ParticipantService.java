package code.store.services;

import code.store.entities.*;
import code.store.repositories.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;


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

    public List<InvitationEntity> getInvitationsForParticipant(Long participantId) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Участник не найден"));

        List<InvitationEntity> pendingInvitations = new ArrayList<>();
        for (InvitationEntity invitation : participant.getInvitations()) {
            if (invitation.getStatus() == InvitationStatus.PENDING) {
                pendingInvitations.add(invitation);
            }
        }
        return pendingInvitations;
    }
}
