package code.api.controllers;

import code.api.dto.OrganizerDto;
import code.api.exceptions.BadRequestException;
import code.api.exceptions.NotFoundException;
import code.api.services.OrganizerService;
import code.store.entities.OrganizerEntity;
import code.store.repositories.OrganizerRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/organizers")
@Transactional
public class OrganizerController {

    OrganizerService organizerService;
    OrganizerRepository organizerRepository;

    @PostMapping
    public ResponseEntity<String> createOrganizer(@RequestBody OrganizerEntity entity) {
        try {
            OrganizerEntity created = organizerService.registerOrganizer(entity);
            return ResponseEntity.ok("Регистрация успешна");
        } catch (BadRequestException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public OrganizerDto login(@RequestBody OrganizerDto dto) {
        OrganizerEntity organizer = organizerRepository
                .findByUsername(dto.getUsername())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!organizer.getPassword().equals(dto.getPassword())) {
            throw new BadRequestException("Неверный пароль");
        }

        OrganizerDto responseDto = new OrganizerDto();
        responseDto.setId(organizer.getId());
        responseDto.setUsername(organizer.getUsername());
        responseDto.setRegistered(organizer.isRegistered());

        return responseDto;
    }

}