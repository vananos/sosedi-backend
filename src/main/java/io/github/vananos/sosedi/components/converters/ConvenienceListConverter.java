package io.github.vananos.sosedi.components.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.vananos.sosedi.models.Convenience;

import java.util.List;

public class ConvenienceListConverter extends BaseJsonConverter<List<Convenience>> {

    public ConvenienceListConverter() {
        super(new TypeReference<List<Convenience>>() {
        });
    }
}
