package code.api.factories;

import code.api.dto.EventDto;
import code.api.dto.InvitationDto;
import code.store.entities.EventEntity;
import code.store.entities.InvitationEntity;
import code.store.entities.InvitationStatus;
import org.springframework.stereotype.Component;

@Component
public class InvitationDtoFactory {

    public InvitationDto makeInvitationDto(InvitationEntity entity){

        return InvitationDto.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .eventId(entity.getEvent().getId())
                .organizerId(entity.getOrganizer().getId())
                .participantId(entity.getParticipant().getId())
                .build();


    }

}
