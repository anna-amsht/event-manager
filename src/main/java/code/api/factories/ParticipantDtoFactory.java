package code.api.factories;

import code.api.dto.OrganizerDto;
import code.api.dto.ParticipantDto;
import code.store.entities.OrganizerEntity;
import code.store.entities.ParticipantEntity;
import org.springframework.stereotype.Component;

@Component
public class ParticipantDtoFactory {

    public ParticipantDto makeParticipantDto(ParticipantEntity entity){

        return ParticipantDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .isRegistered(entity.isRegistered())
                .build();

    }

}
