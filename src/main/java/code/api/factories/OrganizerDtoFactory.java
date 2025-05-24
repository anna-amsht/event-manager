package code.api.factories;

import code.api.dto.EventDto;
import code.api.dto.OrganizerDto;
import code.store.entities.EventEntity;
import code.store.entities.OrganizerEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizerDtoFactory {

    public OrganizerDto makeOrganizerDto(OrganizerEntity entity){

        return OrganizerDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .isRegistered(entity.isRegistered())
                .build();

    }

}
