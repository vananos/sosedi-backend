package io.github.vananos.sosedi.components.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.vananos.sosedi.models.RoomType;

import java.util.List;

public class RoomTypeListConverter extends BaseJsonConverter<List<RoomType>> {

    public RoomTypeListConverter() {
        super(new TypeReference<List<RoomType>>() {
        });
    }
}