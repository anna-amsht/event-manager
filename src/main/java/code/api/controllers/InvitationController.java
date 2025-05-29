package code.api.controllers;

import code.api.dto.InvitationDto;
import code.api.factories.InvitationDtoFactory;
import code.store.entities.InvitationEntity;
import code.api.services.InvitationService;
import code.store.entities.InvitationStatus;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/invitations")
@Transactional
public class InvitationController {

    private final InvitationService invitationService;
    private final InvitationDtoFactory invitationDtoFactory;

    @PostMapping("/send")
    public ResponseEntity<InvitationDto> sendInvitation(
            @RequestParam Long organizerId,
            @RequestParam Long participantId,
            @RequestParam Long eventId
    ) {
        var invitation = invitationService.sendInvitation(organizerId, participantId, eventId);
        return ResponseEntity.ok(invitationDtoFactory.makeInvitationDto(invitation));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<InvitationDto> acceptInvitation(@PathVariable Long id) {
        var updatedInvitation = invitationService.acceptInvitation(id);
        return ResponseEntity.ok(invitationDtoFactory.makeInvitationDto(updatedInvitation));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<InvitationDto> rejectInvitation(@PathVariable Long id) {
        var updatedInvitation = invitationService.rejectInvitation(id);
        return ResponseEntity.ok(invitationDtoFactory.makeInvitationDto(updatedInvitation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable Long id) {
        invitationService.deleteInvitation(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/organizer")
    public ResponseEntity<List<InvitationDto>> getInvitationsByOrganizer(
            @RequestParam Long organizerId) {
        List<InvitationEntity> invitations = invitationService.findInvitationsByOrganizerId(organizerId);
        List<InvitationDto> dtos = invitations.stream()
                .map(invitationDtoFactory::makeInvitationDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @PatchMapping("/{invitationId}/{status}")
    public ResponseEntity<Void> updateInvitationStatus(
            @PathVariable Long invitationId,
            @PathVariable String status
    ) {
        invitationService.updateStatus(invitationId, InvitationStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok().build();
    }
}