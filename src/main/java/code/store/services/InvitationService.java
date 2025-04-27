package code.store.services;

import code.store.entities.*;
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
    private final EventService eventService;

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
                .status(InvitationStatus.PENDING)
                .build();

        invitationRepository.save(invitation);
    }

    public void acceptInvitation(Long invitationId) {
        InvitationEntity invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Приглашение не найдено"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Приглашение уже принято");
        }

        eventService.registerParticipant(invitation.getEvent(), invitation.getParticipant());

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);

    }

    public void deleteInvitation(Long invitationId) {
        if (!invitationRepository.existsById(invitationId)) {
            throw new RuntimeException("Приглашение не найдено");
        }
        invitationRepository.deleteById(invitationId);
    }

}
