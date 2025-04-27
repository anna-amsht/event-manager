package code.api.factories;

import code.api.dto.EventDto;
import code.api.dto.ReservationDto;
import code.store.entities.EventEntity;
import code.store.entities.ReservationEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationDtoFactory {

    public ReservationDto createResrvationDto(ReservationEntity entity){
        return ReservationDto.builder()
                .id(entity.getId())
                .eventId(entity.getEvent().getId())
                .participantId(entity.getParticipant() != null ? entity.getParticipant().getId() : null)
                .build();
    }
}
