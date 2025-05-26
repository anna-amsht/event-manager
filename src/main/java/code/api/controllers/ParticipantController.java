package code.api.controllers;

import code.api.dto.EventDto;
import code.api.dto.InvitationDto;
import code.api.dto.ParticipantDto;
import code.api.exceptions.BadRequestException;
import code.api.exceptions.NotFoundException;
import code.api.factories.EventDtoFactory;
import code.api.factories.InvitationDtoFactory;
import code.api.factories.ParticipantDtoFactory;
import code.api.services.ParticipantService;
import code.store.entities.EventEntity;
import code.store.entities.InvitationEntity;
import code.store.entities.ParticipantEntity;
import code.store.repositories.ParticipantRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RequestMapping("/api/participants")
@RestController
public class ParticipantController {

    ParticipantService participantService;
    ParticipantDtoFactory participantDtoFactory;
    EventDtoFactory eventDtoFactory;
    InvitationDtoFactory invitationDtoFactory;
    ParticipantRepository participantRepository;

    @PostMapping
    public ResponseEntity<String> createParticipant(@RequestBody ParticipantEntity entity) {
        try {
            ParticipantEntity created = participantService.registerParticipant(entity);
            return ResponseEntity.ok("Регистрация прошла успешно");
        } catch (BadRequestException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ParticipantDto login(@RequestBody ParticipantDto dto) {
        ParticipantEntity participant = participantRepository
                .findByUsername(dto.getUsername())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!participant.getPassword().equals(dto.getPassword())) {
            throw new BadRequestException("Неверный пароль");
        }

        return participantDtoFactory.makeParticipantDto(participant);
    }

    @GetMapping("/{participantId}/events")
    public List<EventDto> getEvents(@PathVariable Long participantId) {
        List<EventEntity> events = participantService.getEventsForParticipant(participantId);
        List<EventDto> result = new ArrayList<>();

        for (EventEntity event : events) {
            result.add(eventDtoFactory.makeEventDto(event));
        }

        return result;
    }

    @GetMapping("/{participantId}/invitations")
    public List<InvitationDto> getInvitations(@PathVariable Long participantId) {
        List<InvitationEntity> invitations = participantService.getInvitationsForParticipant(participantId);
        List<InvitationDto> result = new ArrayList<>();

        for (InvitationEntity invitation : invitations) {
            result.add(invitationDtoFactory.makeInvitationDto(invitation));
        }

        return result;
    }
}
