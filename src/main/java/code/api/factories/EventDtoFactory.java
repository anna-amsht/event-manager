package code.api.factories;

import code.api.dto.EventDto;
import code.store.entities.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventDtoFactory {

    public EventDto makeEventDto(EventEntity entity){

        return EventDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .location(entity.getLocation())
                .dateTime(entity.getDateTime())
                .numberOfSeats(entity.getNumberOfSeats())
                .organizerId(entity.getOrganizer() != null ? entity.getOrganizer().getId() : null)
                .build();

    }

}
