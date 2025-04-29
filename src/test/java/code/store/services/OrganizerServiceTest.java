package code.store.services;

import code.store.entities.EventEntity;
import code.store.entities.OrganizerEntity;
import code.store.repositories.OrganizerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizerServiceTest {
    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private OrganizerService organizerService;

    private OrganizerEntity organizer;
    private EventEntity event;

    @BeforeEach
    void setup() {
        event = new EventEntity();
        organizer = new OrganizerEntity();
        organizer.setId(1L);

        event.setId(2L);
        event.setTitle("title");
        event.setDescription("this is description");
        event.setDateTime(LocalDateTime.now());
        event.setLocation("New York");
        event.setNumberOfSeats(20);
        event.setOrganizer(organizer);
    }

    @Test
    void testReegicterOrganizer() {
        when(organizerRepository.save(organizer)).thenReturn(organizer);

        OrganizerEntity result = organizerService.registerOrganizer(organizer);
        Assertions.assertEquals(result, organizer);

        verify(organizerRepository).save(organizer);
    }

    @Test
    void testCreateEvent() {
        when(organizerRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(eventService.createEvent(event)).thenReturn(event);

        EventEntity result = organizerService.createEvent(1L, event);

        Assertions.assertEquals(event, result);
        Assertions.assertEquals(organizer, result.getOrganizer());

        verify(eventService).createEvent(event);
    }

    @Test
    void testCreateEvent_OrganizerNotExist() {
        when(organizerRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> organizerService.createEvent(1L, event));
    }

    @Test
    void testUpdateEvent() {
        EventEntity updatedEvent = new EventEntity();
        updatedEvent.setId(2L);
        updatedEvent.setTitle("Updated");
        updatedEvent.setDescription("Updated Desc");
        updatedEvent.setDateTime(LocalDateTime.now());
        updatedEvent.setLocation("Updated Location");
        updatedEvent.setNumberOfSeats(150);

        when(organizerRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(eventService.getEventById(2L)).thenReturn(event);
        when(eventService.createEvent(event)).thenReturn(updatedEvent);

        EventEntity result = organizerService.updateEvent(1L, updatedEvent);

        Assertions.assertEquals("Updated", result.getTitle());
        verify(eventService).createEvent(event);
    }

    @Test
    void testUpdateEvent_OrganizerMismatch() {
        OrganizerEntity otherOrganizer = new OrganizerEntity();
        otherOrganizer.setId(3L);

        when(organizerRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(eventService.getEventById(2L)).thenReturn(event);
        event.setOrganizer(otherOrganizer);

        Assertions.assertThrows(RuntimeException.class, () -> organizerService.updateEvent(1L, event));
    }

    @Test
    void testDeleteEvent(){
        when(organizerRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(eventService.getEventById(2L)).thenReturn(event);

        organizerService.deleteEvent(1L, 2L);

        verify(eventService).deleteEvent(2L);
    }

    @Test
    void testDeleteEvent_OrganizerMismatch(){
        OrganizerEntity otherOrganizer =new OrganizerEntity();
        otherOrganizer.setId(3L);
        event.setOrganizer(otherOrganizer);

        when(organizerRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(eventService.getEventById(2L)).thenReturn(event);

        Assertions.assertThrows(RuntimeException.class, () -> organizerService.deleteEvent(1L, 2L));

    }
}
