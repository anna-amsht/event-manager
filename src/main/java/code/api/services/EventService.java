package code.api.services;

import code.store.entities.EventEntity;
import code.store.entities.OrganizerEntity;
import code.store.entities.ParticipantEntity;
import code.store.entities.ReservationEntity;
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
        for (ReservationEntity reservation : event.getReservations()) {
            if (reservation.getParticipant().getId().equals(participant.getId())) {
                throw new RuntimeException("Участник уже зарегистрирован на это мероприятие");
            }
        }

        if (event.getNumberOfSeats() <= 0) {
            throw new RuntimeException("Нет доступных мест для регистрации");
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.setEvent(event);
        reservation.setParticipant(participant);

        event.getReservations().add(reservation);

        event.setNumberOfSeats(event.getNumberOfSeats() - 1);


        eventRepository.save(event);
    }

}
