package io.github.vananos.sosedi.components.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.vananos.sosedi.models.Interests;

import java.util.List;

public class InterestListConverter extends BaseJsonConverter<List<Interests>> {
    @Override
    protected TypeReference<List<Interests>> getTypeReference() {
        return new TypeReference<List<Interests>>() {
        };
    }
}
