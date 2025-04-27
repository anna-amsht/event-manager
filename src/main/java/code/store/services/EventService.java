package code.store.services;

import code.store.entities.EventEntity;
import code.store.entities.OrganizerEntity;
import code.store.entities.ParticipantEntity;
import code.store.repositories.EventRepository;
import code.store.repositories.OrganizerRepository;
import code.store.repositories.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final ParticipantRepository participantRepository;

    public EventEntity createEvent(EventEntity eventEntity) {

        OrganizerEntity organizer = organizerRepository.findById(eventEntity.getOrganizer().getId())
                .orElseThrow(() -> new RuntimeException("Организатор не найден"));
        eventEntity.setOrganizer(organizer);

        return eventRepository.save(eventEntity);

    }

    public List<EventEntity> getAllEvents() {
        return eventRepository.findAll();
    }

    public EventEntity getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Мероприятие не найдено"));
    }

    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("Мероприятие уже удалено");
        }
        eventRepository.deleteById(eventId);
    }

    public void registerParticipant(EventEntity event, ParticipantEntity participant) {
        if (event.getParticipants().contains(participant)) {
            throw new RuntimeException("Участник уже зарегистрирован на это мероприятие");
        }
        if (event.getNumberOfSeats() <= 0) {
            throw new RuntimeException("Нет доступных мест для регистрации");
        }
        event.getParticipants().add(participant);
        event.setNumberOfSeats(event.getNumberOfSeats() - 1);

        eventRepository.save(event);
    }

}
