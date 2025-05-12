package code.store.services;

import code.api.services.ParticipantService;
import code.store.entities.*;
import code.store.repositories.ParticipantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {
    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    private ParticipantEntity participant;


    @BeforeEach
    void setup(){
        participant = new ParticipantEntity();
        participant.setId(1L);
    }

    @Test
    void testRegisterParticipant(){
        when(participantRepository.save(participant)).thenReturn(participant);

        ParticipantEntity result = participantService.registerParticipant(participant);
        Assertions.assertEquals(result, participant);

        verify(participantRepository).save(participant);
    }

    @Test
    void  testGetEvents(){
        EventEntity event1 = new EventEntity();
        event1.setId(2L);
        EventEntity event2 = new EventEntity();
        event2.setId(3L);

        ReservationEntity reservation1 = new ReservationEntity();
        reservation1.setEvent(event1);
        ReservationEntity reservation2 =new ReservationEntity();
        reservation2.setEvent(event2);

        List<ReservationEntity> reservations =  List.of(reservation1, reservation2);
        participant.setReservations(reservations);

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        List<EventEntity> events = participantService.getEventsForParticipant(1L);

        Assertions.assertEquals(2, events.size());
        Assertions.assertTrue(events.contains(event1));
        Assertions.assertTrue(events.contains(event2));

    }

    @Test
    void testGetEvents_ParticipantNotFound(){
        when(participantRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> participantService.getEventsForParticipant(1L));
    }

    @Test
    void testGetInvitations(){
        InvitationEntity invitation1 = new InvitationEntity();
        invitation1.setStatus(InvitationStatus.PENDING);

        InvitationEntity invitation2 = new InvitationEntity();
        invitation2.setStatus(InvitationStatus.ACCEPTED);

        InvitationEntity invitation3 = new InvitationEntity();
        invitation3.setStatus(InvitationStatus.PENDING);

        List<InvitationEntity> invitations = List.of(invitation1, invitation2, invitation3);

        participant.setInvitations(invitations);

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        List<InvitationEntity> result = participantService.getInvitationsForParticipant(1L);

        Assertions.assertEquals(2, result.size());

        for (InvitationEntity invitation : result) {
            Assertions.assertEquals(InvitationStatus.PENDING, invitation.getStatus());
        }

    }

    @Test
    void testGetInvitations_ParticipantNotFound(){
        when(participantRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> participantService.getInvitationsForParticipant(1L));

    }
}
