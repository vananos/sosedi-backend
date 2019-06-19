package io.github.vananos.sosedi.components.converters;

import io.github.vananos.sosedi.models.GeoInfo;

public class GeoInfoConverter extends BaseJsonConverter<GeoInfo> {
    @Override
    protected Class<GeoInfo> getBeanClass() {
        return GeoInfo.class;
    }
}
