package io.github.vananos.sosedi.components.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.vananos.sosedi.models.GeoInfo;

public class GeoInfoConverter extends BaseJsonConverter<GeoInfo> {

    public GeoInfoConverter() {
        super(new TypeReference<GeoInfo>() {
        });
    }
}
