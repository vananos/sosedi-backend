package io.github.vananos.sosedi.components.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.vananos.sosedi.models.Convenience;

import java.util.List;

public class ConvenienceListConverter extends BaseJsonConverter<List<Convenience>> {
    @Override
    protected TypeReference<List<Convenience>> getTypeReference() {
        return new TypeReference<List<Convenience>>() {
        };
    }
}
