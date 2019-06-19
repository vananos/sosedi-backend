package io.github.vananos.sosedi.components.converters;

import java.util.List;

public class ListConverter extends BaseJsonConverter<List> {

    @Override
    protected Class<List> getBeanClass() {
        return List.class;
    }
}