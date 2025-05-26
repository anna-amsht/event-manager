package code.api.controllers;


import code.api.dto.EventDto;
import code.api.factories.EventDtoFactory;
import code.store.entities.EventEntity;
import code.store.entities.OrganizerEntity;
import code.api.services.EventService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RequestMapping("/api/events")
@RestController
public class EventController {

    EventService eventService;
    EventDtoFactory eventDtoFactory;

    @PostMapping
    public EventDto createEvent(@RequestBody EventDto dto){
        EventEntity event = new EventEntity();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setDateTime(dto.getDateTime());
        event.setNumberOfSeats(dto.getNumberOfSeats());
        event.setFormat(dto.getFormat());

        OrganizerEntity organizer = new OrganizerEntity();
        organizer.setId(dto.getOrganizerId());
        event.setOrganizer(organizer);

        EventEntity savedEvent = eventService.createEvent(event);

        return eventDtoFactory.makeEventDto(savedEvent);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @RequestBody @Valid EventDto eventDto,
            BindingResult bindingResult) {

        // Валидация входных данных
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            EventEntity updatedEvent = eventService.updateEvent(id, eventDto);
            return ResponseEntity.ok(eventDtoFactory.makeEventDto(updatedEvent));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Ошибка целостности данных: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка сервера: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable Long id) {
        EventEntity event = eventService.getEventById(id);
        return eventDtoFactory.makeEventDto(event);
    }

    @GetMapping
    public List<EventDto> getAllEvents() {
        List<EventDto> eventDtos = new ArrayList<>();
        List<EventEntity> events = eventService.getAllEvents();
        for (EventEntity event : events) {
            eventDtos.add(eventDtoFactory.makeEventDto(event));
        }
        return eventDtos;
    }
    @GetMapping("/by-organizer")
    public List<EventDto> getEventsByOrganizer(@RequestParam Long organizerId) {
        List<EventDto> eventDtos = new ArrayList<>();
        List<EventEntity> events = eventService.getEventsByOrganizerId(organizerId);
        for (EventEntity event : events) {
            eventDtos.add(eventDtoFactory.makeEventDto(event));
        }
        return eventDtos;
    }


    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}