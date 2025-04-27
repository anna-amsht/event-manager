package code.api.factories;

import code.api.dto.EventDto;
import code.api.dto.ParticipantDto;
import code.store.entities.EventEntity;
import code.store.entities.ParticipantEntity;
import org.springframework.stereotype.Component;

@Component
public class ParticipantDtoFactory {

    public ParticipantDto createParticipantDto(ParticipantEntity entity){
        return ParticipantDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .build();

    }
}
