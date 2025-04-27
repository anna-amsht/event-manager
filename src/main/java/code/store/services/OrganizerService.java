package code.store.services;

import code.store.entities.EventEntity;
import code.store.entities.OrganizerEntity;
import code.store.repositories.EventRepository;
import code.store.repositories.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private  final OrganizerRepository organizerRepository;
    private final EventService eventService;

    public OrganizerEntity registerOrganizer(OrganizerEntity organizerEntity) {
        return organizerRepository.save(organizerEntity);
    }

    public EventEntity createEvent(Long organizerId, EventEntity eventEntity) {
        OrganizerEntity organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Организатор не найден"));

        eventEntity.setOrganizer(organizer);
        return eventService.createEvent(eventEntity);
    }

    public EventEntity updateEvent(Long organizerId, EventEntity updatedEvent) {

        OrganizerEntity organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Организатор не найден"));


        EventEntity existingEvent = eventService.getEventById(updatedEvent.getId());

        if (!existingEvent.getOrganizer().getId().equals(organizer.getId())) {
            throw new RuntimeException("Организатор не имеет права обновлять это мероприятие");
        }

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setDateTime(updatedEvent.getDateTime());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setNumberOfSeats(updatedEvent.getNumberOfSeats());

        return eventService.createEvent(existingEvent);
    }

    public void deleteEvent(Long organizerId, Long eventId) {
        OrganizerEntity organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Организатор не найден"));

        EventEntity event = eventService.getEventById(eventId);

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new RuntimeException("Организатор не имеет права удалить это мероприятие");
        }

        eventService.deleteEvent(eventId);
    }
}
