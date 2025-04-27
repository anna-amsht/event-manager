package code.api.factories;

import code.api.dto.EventDto;
import code.store.entities.EventEntity;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

@Component
public class EventDtoFactory {

    public EventDto createEventDto(EventEntity entity){
        return EventDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .numberOfSeats(entity.getNumberOfSeats())
                .dateTime(entity.getDateTime())
                .location(entity.getLocation())
                .organizerId(entity.getOrganizer() != null ? entity.getOrganizer().getId() : null)
                .build();

    }
}
