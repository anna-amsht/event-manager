package code.store.services;

import code.store.entities.EventEntity;
import code.store.entities.OrganizerEntity;
import code.store.repositories.EventRepository;
import code.store.repositories.InvitationRepository;
import code.store.repositories.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private  final OrganizerRepository organizerRepository;
    private final EventRepository eventRepository;

    public OrganizerEntity registerOrganizer(OrganizerEntity organizerEntity) {
        return organizerRepository.save(organizerEntity);
    }

    public EventEntity createEvent(Long organizerId, EventEntity event) {
        OrganizerEntity organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Организатор не найден"));

        event.setOrganizer(organizer); // привязываем организатора к мероприятию
        return eventRepository.save(event);
    }

    public EventEntity updateEvent(EventEntity event) {
        if (eventRepository.existsById(event.getId())) {
            return eventRepository.save(event);
        } else {
            throw new IllegalArgumentException("Мероприятие не найдено для обновления");
        }
    }

    public void deleteEvent(Long eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
        } else {
            throw new IllegalArgumentException("Мероприятие не найдено для удаления");
        }
    }
}
