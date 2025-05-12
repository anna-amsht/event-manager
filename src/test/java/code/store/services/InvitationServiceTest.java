package code.store.services;

import code.api.services.EventService;
import code.api.services.InvitationService;
import code.store.entities.*;
import code.store.repositories.EventRepository;
import code.store.repositories.InvitationRepository;
import code.store.repositories.OrganizerRepository;
import code.store.repositories.ParticipantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvitationServiceTest {

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private InvitationService invitationService;

    private OrganizerEntity organizer;
    private ParticipantEntity participant;
    private EventEntity event;
    private InvitationEntity invitation;

    @BeforeEach
    void setup() {

        organizer = new OrganizerEntity();
        organizer.setId(1L);

        participant = new ParticipantEntity();
        participant.setId(2L);

        event = new EventEntity();
        event.setId(3L);


        invitation = InvitationEntity.builder()
                .id(4L)
                .organizer(organizer)
                .participant(participant)
                .event(event)
                .status(InvitationStatus.PENDING)
                .build();
    }
    @Test
    void testSendInvitation() {

        when(organizerRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(participantRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(eventRepository.findById(3L)).thenReturn(Optional.of(event));


        invitationService.sendInvitation(1L, 2L, 3L);


        ArgumentCaptor<InvitationEntity> captor = ArgumentCaptor.forClass(InvitationEntity.class);
        verify(invitationRepository, times(1)).save(captor.capture());

        InvitationEntity savedInvitation = captor.getValue();
        assertEquals(organizer, savedInvitation.getOrganizer());
        assertEquals(participant, savedInvitation.getParticipant());
        assertEquals(event, savedInvitation.getEvent());
        assertEquals(InvitationStatus.PENDING, savedInvitation.getStatus());
    }

    @Test
    void testAcceptInvitation(){


        when(invitationRepository.findById(4L)).thenReturn(Optional.of(invitation));
        invitationService.acceptInvitation(4L);

        Assertions.assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
        verify(eventService).registerParticipant(event, participant);
        verify(invitationRepository).save(invitation);
    }

    @Test
    void testAcceptInvitation_AlreadyAccept(){
        invitation.setStatus(InvitationStatus.ACCEPTED);
        when(invitationRepository.findById(4L)).thenReturn(Optional.of(invitation));

        Assertions.assertThrows(RuntimeException.class, () -> invitationService.acceptInvitation(4L));
    }

    @Test
    void testDeleteInvitation(){
        when(invitationRepository.existsById(4L)).thenReturn(true);
        invitationService.deleteInvitation(4L);
        verify(invitationRepository).deleteById(4L);
    }

    @Test
    void testDelete_AlreadyDeleted(){
        when(invitationRepository.existsById(4L)).thenReturn(false);
        Assertions.assertThrows(RuntimeException.class, () -> invitationService.deleteInvitation(4L));
    }




}
