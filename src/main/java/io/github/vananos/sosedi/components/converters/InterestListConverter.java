package io.github.vananos.sosedi.components.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.vananos.sosedi.models.Interests;

import java.util.List;

public class InterestListConverter extends BaseJsonConverter<List<Interests>> {

    public InterestListConverter() {
        super(new TypeReference<List<Interests>>() {
        });
    }
}
