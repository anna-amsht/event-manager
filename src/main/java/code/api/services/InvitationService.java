package code.api.services;

import code.api.exceptions.NotFoundException;
import code.store.entities.*;
import code.store.repositories.EventRepository;
import code.store.repositories.InvitationRepository;
import code.store.repositories.OrganizerRepository;
import code.store.repositories.ParticipantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private  final InvitationRepository invitationRepository;
    private final OrganizerRepository organizerRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final EventService eventService;

    public InvitationEntity sendInvitation(Long organizerId, Long participantId, Long eventId) {

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

       return invitationRepository.save(invitation);
    }

    public InvitationEntity acceptInvitation(Long invitationId) {
        InvitationEntity invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Приглашение не найдено"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Приглашение уже принято");
        }

        eventService.registerParticipant(invitation.getEvent(), invitation.getParticipant());

        invitation.setStatus(InvitationStatus.ACCEPTED);
        return invitationRepository.save(invitation);

    }
    public InvitationEntity rejectInvitation(Long id) {
        InvitationEntity invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

        invitation.setStatus(InvitationStatus. DECLINED);
        return invitationRepository.save(invitation);
    }

    public void deleteInvitation(Long invitationId) {
        if (!invitationRepository.existsById(invitationId)) {
            throw new RuntimeException("Приглашение не найдено");
        }
        invitationRepository.deleteById(invitationId);
    }

    public void updateStatus(Long invitationId, InvitationStatus status) {
        InvitationEntity invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotFoundException("Приглашение не найдено"));

        invitation.setStatus(status);
        invitationRepository.save(invitation);
    }
    public List<InvitationEntity> findInvitationsByOrganizerId(Long organizerId) {
        if (!organizerRepository.existsById(organizerId)) {
            throw new RuntimeException("Организатор не найден");
        }
        return invitationRepository.findByOrganizerId(organizerId);
    }

}
