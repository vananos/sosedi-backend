package io.github.vananos.sosedi.components.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public abstract class BaseJsonConverter<T> implements AttributeConverter<T, String> {
    private Class<T> beanClass = getBeanClass();

    abstract protected Class<T> getBeanClass();

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
            return objectMapper.readValue(dbData, beanClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}