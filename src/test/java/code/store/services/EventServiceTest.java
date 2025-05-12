package code.store.services;

import code.api.services.EventService;
import code.store.entities.EventEntity;
import code.store.entities.OrganizerEntity;
import code.store.entities.ParticipantEntity;
import code.store.entities.ReservationEntity;
import code.store.repositories.EventRepository;
import code.store.repositories.OrganizerRepository;
import code.store.repositories.ParticipantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void testCreateEvent() {
        OrganizerEntity organizer = new OrganizerEntity();
        organizer.setId(1L);

        EventEntity event = new EventEntity();
        event.setOrganizer(organizer);

        when(organizerRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(eventRepository.save(event)).thenReturn(event);

        EventEntity result = eventService.createEvent(event);

        assertEquals(organizer, result.getOrganizer());
        verify(eventRepository).save(event);
    }

    @Test
    void testGetAllEvents(){
        List<EventEntity> events = List.of(new EventEntity(),  new EventEntity());

        when(eventRepository.findAll()).thenReturn(events);

        List<EventEntity> result = eventService.getAllEvents();

        assertEquals(events, result);
        verify(eventRepository).findAll();
    }

    @Test
    void testGetEventById(){
        EventEntity event = new EventEntity();

        event.setId(1L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventEntity result = eventService.getEventById(1L);
        assertEquals(event, result);
        verify(eventRepository).findById(1L);
    }

    @Test
    void testDeleteEvent(){
       when(eventRepository.existsById(1L)).thenReturn(true);

       eventService.deleteEvent(1L);

       verify(eventRepository).deleteById(1l);
    }

    @Test
    void testDeletAlradyDeletedEvent(){
        when(eventRepository.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(RuntimeException.class, () -> eventService.deleteEvent(1L));

    }
    @Test
    void testRegisterParticipant(){

        ParticipantEntity participant = new ParticipantEntity();
        EventEntity event = new EventEntity();
        event.setReservations(new ArrayList<>());
        event.setNumberOfSeats(1);

        eventService.registerParticipant(event, participant);

        boolean found = false;
        for (ReservationEntity reservation : event.getReservations()) {
            if (reservation.getParticipant().equals(participant)) {
                found = true;
                break;
            }
        }

        Assertions.assertTrue(found);
        Assertions.assertEquals(0, event.getNumberOfSeats());
        verify(eventRepository).save(event);
    }

    @Test
    void testRegisterParticipantForNotFoundEvent(){
        ParticipantEntity participant = new ParticipantEntity();
        EventEntity event = new EventEntity();
        event.setNumberOfSeats(0);

        Assertions.assertThrows(RuntimeException.class, () -> eventService.registerParticipant(event,participant));
    }

    @Test
    void testRegisterParticipantAlreadyRegistered(){
        ParticipantEntity participant = new ParticipantEntity();
        EventEntity event = new EventEntity();
        ReservationEntity reservation = new ReservationEntity();

        reservation.setParticipant(participant);

        List<ReservationEntity> reservations = new ArrayList<>();
        reservations.add(reservation);

        event.setReservations(reservations);

        Assertions.assertThrows(RuntimeException.class, () ->eventService.registerParticipant(event, participant));
    }

}
