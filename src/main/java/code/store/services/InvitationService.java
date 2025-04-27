package code.store.services;

import code.store.entities.EventEntity;
import code.store.entities.InvitationEntity;
import code.store.entities.OrganizerEntity;
import code.store.entities.ParticipantEntity;
import code.store.repositories.EventRepository;
import code.store.repositories.InvitationRepository;
import code.store.repositories.OrganizerRepository;
import code.store.repositories.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private  final InvitationRepository invitationRepository;
    private final OrganizerRepository organizerRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public void sendInvitation(Long organizerId, Long participantId, Long eventId) {

        OrganizerEntity organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Организатор не найден"));

        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Участник не найден"));

        EventEntity event =  eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Мероприятие не найдено"));

        InvitationEntity invitation =  InvitationEntity.builder()
                .organizer(organizer)
                .participant(participant)
                .event(event)
                .build();

        invitationRepository.save(invitation);
    }

    public void deleteInvitation(Long invitationId) {
        if (!invitationRepository.existsById(invitationId)) {
            throw new RuntimeException("Приглашение не найдено");
        }
        invitationRepository.deleteById(invitationId);
    }

}
