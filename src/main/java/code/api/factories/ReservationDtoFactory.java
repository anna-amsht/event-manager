package code.api.factories;

import code.api.dto.OrganizerDto;
import code.api.dto.ReservationDto;
import code.store.entities.OrganizerEntity;
import code.store.entities.ReservationEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationDtoFactory {

    public ReservationDto makeReservationDto(ReservationEntity entity){

        return ReservationDto.builder()
                .id(entity.getId())
                .participantId(entity.getParticipant().getId())
                .eventId(entity.getEvent().getId())
                .build();

    }

}
