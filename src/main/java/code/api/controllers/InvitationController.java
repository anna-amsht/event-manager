package code.api.controllers;

import code.api.dto.InvitationDto;
import code.api.factories.InvitationDtoFactory;
import code.store.entities.InvitationEntity;
import code.api.services.InvitationService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/invitaions")
@Transactional
public class InvitationController {

    InvitationService invitationService;
    InvitationDtoFactory invitationDtoFactory;

    // TODO: в InvitationService заменить void на EventEntity
//    @PostMapping("/send")
//    public InvitationDto sendInvitation(@RequestBody InvitationDto dto) {
//        InvitationEntity invitation = invitationService.sendInvitation(
//                dto.getOrganizerId(),
//                dto.getParticipantId(),
//                dto.getEventId()
//        );
//        return invitationDtoFactory.makeInvitationDto(invitation);
//    }
    // TODO: в InvitationService заменить void на EventEntity
//    @PostMapping("accept/{id}")
//    public InvitationDto acceptInvitation(@PathVariable("id") Long id) {
//        InvitationEntity invitation = invitationService.acceptInvitation(id);
//        return invitationDtoFactory.makeInvitationDto(invitation);
//    }
    @DeleteMapping("/{id}")
    public void deleteInvitation(@PathVariable Long id){
        invitationService.deleteInvitation(id);
    }
}
