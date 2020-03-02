package io.github.vananos.sosedi.components.mappers;

import io.github.vananos.sosedi.models.RegistrationInfo;
import io.github.vananos.sosedi.models.dto.registration.RegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    RegistrationInfo toRegistrationInfo(RegistrationRequest registrationRequest);
}