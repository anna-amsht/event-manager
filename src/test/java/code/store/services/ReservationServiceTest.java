package code.store.services;

import code.store.entities.EventEntity;
import code.store.entities.ParticipantEntity;
import code.store.entities.ReservationEntity;
import code.store.repositories.EventRepository;
import code.store.repositories.ParticipantRepository;
import code.store.repositories.ReservationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ReservationService reservationService;

    private EventEntity event;
    private ParticipantEntity participant;

    @BeforeEach
    void setup(){
        event = new EventEntity();
        event.setId(1L);
        event.setNumberOfSeats(5);

        participant = new ParticipantEntity();
        participant.setId(2L);
    }

    @Test
    void testCreateReservation(){
        when(participantRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(reservationRepository.existsByParticipantAndEvent(participant,event)).thenReturn(false);

        reservationService.createReservation(2L, 1L);
        verify(reservationRepository).save(any(ReservationEntity.class));
        verify(eventRepository).save(any(EventEntity.class));
    }
    @Test
    void testCreateReservation_ParticipantNotFound(){
        when(participantRepository.findById(2L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> reservationService.createReservation(2L,1L));
    }

    @Test
    void testCreateReservation_EventNotFound(){
        when(participantRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> reservationService.createReservation(2L, 1L));
    }

    @Test
    void testCreateReservation_NoSeats(){
        event.setNumberOfSeats(0);

        when(participantRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Assertions.assertThrows(RuntimeException.class, () -> reservationService.createReservation(2L, 1L));
    }

    @Test
    void testCreateReservation_AlreadyReserved(){
        when(participantRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(reservationRepository.existsByParticipantAndEvent(participant,event)).thenReturn(true);

        Assertions.assertThrows(RuntimeException.class, () -> reservationService.createReservation(2L, 1L));
    }

    @Test
    void testCancelReservation(){
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(3L);
        reservation.setEvent(event);

        when(reservationRepository.findById(3L)).thenReturn(Optional.of(reservation));
        reservationService.cancelReservation(3L);

        verify(eventRepository).save(argThat(e -> e.getNumberOfSeats() == 6));
        verify(reservationRepository).deleteById(3L);
    }


    @Test
    void testCancelReservation_NotFound(){
        when(reservationRepository.findById(3L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> reservationService.cancelReservation(3L));
    }


}
