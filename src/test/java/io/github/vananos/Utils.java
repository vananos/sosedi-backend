package io.github.vananos;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.vananos.sosedi.models.*;
import io.github.vananos.sosedi.models.GeoInfo.Coordinates;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static io.github.vananos.sosedi.models.RoomType.SINGLE;
import static java.util.Arrays.asList;

public class Utils {
    public static final String AD_DESCRIPTION = "description about myself";

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
        expectedUser.setInterests(Collections.emptyList());
        expectedUser.setGender(Gender.MALE);
        return expectedUser;
    }


    public static Advertisement getValidAdvertisement() {
        Advertisement ad = new Advertisement();
        ad.setId(1L);
        ad.setAnimals(Attitude.GOOD);
        ad.setSmoking(Attitude.BAD);
        ad.setDescription(AD_DESCRIPTION);
        ad.setLandlord(false);
        ad.setConveniences(asList(Convenience.TV));
        ad.setGender(Gender.ANY);
        ad.setMinAge(18);
        ad.setMaxAge(40);
        ad.setRentPay(15000);
        ad.setPlaceId(getValidGeoInfo());
        ad.setRoomType(asList(SINGLE));
        return ad;
    }

    public static GeoInfo getValidGeoInfo() {
        GeoInfo geoInfo = new GeoInfo();
        geoInfo.setAddress("SPB");
        geoInfo.setLatLng(new Coordinates());
        geoInfo.getLatLng().setLat(12.23);
        geoInfo.getLatLng().setLng(15.48);
        return geoInfo;
    }
}
