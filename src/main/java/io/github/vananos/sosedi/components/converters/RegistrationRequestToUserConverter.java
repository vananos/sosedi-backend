package io.github.vananos.sosedi.components.converters;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.registration.RegistrationRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RegistrationRequestToUserConverter implements Converter<RegistrationRequest, User> {
    @Override
    public User convert(RegistrationRequest source) {
        User user = new User();
        user.setEmail(source.getEmail());
        user.setName(source.getName());
        user.setSurname(source.getSurname());
        user.setPassword(source.getPassword());
        return user;
    }
}