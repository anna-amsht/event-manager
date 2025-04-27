package code.api.factories;

import code.api.dto.EventDto;
import code.api.dto.InvitationDto;
import code.store.entities.EventEntity;
import code.store.entities.InvitationEntity;
import org.springframework.stereotype.Component;

@Component
public class InvitationDtoFactory {

    public InvitationDto createInvitationDto(InvitationEntity entity){
        return InvitationDto.builder()
                .id(entity.getId())
                .eventId(entity.getEvent().getId())
                .organizerId(entity.getOrganizer()!= null ? entity.getOrganizer().getId() : null)
                .participantId(entity.getParticipant() != null ? entity.getParticipant().getId() : null)
                .build();

    }
}
