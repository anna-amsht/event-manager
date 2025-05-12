package code.api.services;

import code.store.entities.EventEntity;
import code.store.entities.ParticipantEntity;
import code.store.entities.ReservationEntity;
import code.store.repositories.EventRepository;
import code.store.repositories.ParticipantRepository;
import code.store.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;


    public void createReservation(Long participantId, Long eventId) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Участник не найден"));

        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Мероприятие не найдено"));

        if (event.getNumberOfSeats() <= 0) {
            throw new RuntimeException("Нет доступных мест для регистрации");
        }

        boolean alreadyReserved = reservationRepository.existsByParticipantAndEvent(participant, event);
        if (alreadyReserved) {
            throw new RuntimeException("Участник уже зарегистрирован на это мероприятие");
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.setParticipant(participant);
        reservation.setEvent(event);

        reservationRepository.save(reservation);

        event.setNumberOfSeats(event.getNumberOfSeats() - 1);
        eventRepository.save(event);
    }

    public void cancelReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Бронь не найдена"));

        EventEntity event = reservation.getEvent();

        event.setNumberOfSeats(event.getNumberOfSeats() + 1);
        eventRepository.save(event);

        reservationRepository.deleteById(reservationId);
    }
}
