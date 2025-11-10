package code.api.services;

import code.api.dto.ParticipantDto;
import code.api.exceptions.NotFoundException;
import code.store.entities.*;
import code.store.repositories.InvitationRepository;
import code.store.repositories.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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



    public Optional<ParticipantEntity> getByUsername(String username) {
        return participantRepository.findByUsername(username);
    }

    public ParticipantEntity getOrCreateByUsername(String username) {
        return participantRepository.findByUsername(username)
                .orElseGet(() -> {
                    ParticipantEntity participant = new ParticipantEntity();
                    participant.setUsername(username);
                    participant.setPassword("default");
                    participant.setRegistered(true);
                    return participantRepository.save(participant);
                });
    }

}
