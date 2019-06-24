package io.github.vananos.sosedi.components.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public abstract class BaseJsonConverter<T> implements AttributeConverter<T, String> {
    private final TypeReference<T> typeReference;

    protected BaseJsonConverter(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(T attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}