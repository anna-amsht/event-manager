package code.api.controllers;

import code.store.entities.OrganizerEntity;
import code.store.repositories.OrganizerRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    OrganizerRepository organizerRepository;

    @PostMapping
    public OrganizerEntity createOrganizer(@RequestBody OrganizerEntity entity){
        return organizerRepository.save(entity);
    }
}