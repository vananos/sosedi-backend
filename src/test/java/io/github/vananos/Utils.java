package io.github.vananos;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.vananos.sosedi.models.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static String toJson(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(value));
            }
        });
        objectMapper.registerModule(simpleModule);
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String jsonResponse, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(value));
            }
        });
        objectMapper.registerModule(simpleModule);
        try {
            return objectMapper.readValue(jsonResponse, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String jsonResponse, TypeReference<T> typeReference) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(value));
            }
        });
        objectMapper.registerModule(simpleModule);
        try {
            return objectMapper.readValue(jsonResponse, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static User getValidUser() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setName("testUser");
        expectedUser.setSurname("testSurName");
        expectedUser.setPassword("somePassword");
        expectedUser.setUserStatus(User.UserStatus.EMAIL_CONFIRMED);
        expectedUser.setBirthday(LocalDate.of(2000, 11, 21));
        expectedUser.setDescription("About myself");
        expectedUser.setEmail("someemail@gmail.com");
        expectedUser.setPhone("+7 (999) 222 33 44");
        expectedUser.setInterests("");
        return expectedUser;
    }
}
