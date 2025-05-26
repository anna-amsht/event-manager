package code.api.controllers;

import code.api.dto.ReservationDto;
import code.api.factories.ReservationDtoFactory;
import code.api.services.ReservationService;
import code.store.entities.ReservationEntity;
import code.store.repositories.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ReservationController {

    ReservationService reservationService;
    ReservationRepository reservationRepository;
    ReservationDtoFactory reservationDtoFactory;

    @PostMapping
    public ReservationDto createReservation(@RequestParam Long participantId, @RequestParam Long eventId) {
        reservationService.createReservation(participantId, eventId);

        List<ReservationEntity> all = reservationRepository.findAll();
        ReservationEntity createdReservation = null;

        for (ReservationEntity reservation : all) {
            if (reservation.getParticipant().getId().equals(participantId)
                    && reservation.getEvent().getId().equals(eventId)) {
                createdReservation = reservation;
            }
        }

        if (createdReservation == null) {
            throw new RuntimeException("Созданная бронь не найдена");
        }

        return reservationDtoFactory.makeReservationDto(createdReservation);
    }

    @DeleteMapping("/{reservationId}")
    public ReservationDto cancelReservation(@PathVariable Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Бронь не найдена"));

        reservationService.cancelReservation(reservationId);
        return reservationDtoFactory.makeReservationDto(reservation);
    }

    @GetMapping
    public List<ReservationDto> getAllReservations() {
        List<ReservationEntity> reservations = reservationRepository.findAll();
        List<ReservationDto> result = new ArrayList<>();

        for (ReservationEntity reservation : reservations) {
            result.add(reservationDtoFactory.makeReservationDto(reservation));
        }

        return result;
    }
}
