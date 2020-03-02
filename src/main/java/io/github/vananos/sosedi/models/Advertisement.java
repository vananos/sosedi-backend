package io.github.vananos.sosedi.models;

import io.github.vananos.sosedi.components.converters.ConvenienceListConverter;
import io.github.vananos.sosedi.components.converters.GeoInfoConverter;
import io.github.vananos.sosedi.components.converters.RoomTypeListConverter;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "ADVERTISEMENTS")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GENDER")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "LANDLORD")
    private Boolean landlord;

    @Column(name = "PLACE_ID", columnDefinition = "TEXT")
    @Convert(converter = GeoInfoConverter.class)
    private GeoInfo placeId;

    @Column(name = "SMOKING")
    @Enumerated(EnumType.STRING)
    private Attitude Smoking;

    @Column(name = "ANIMALS")
    @Enumerated(EnumType.STRING)
    private Attitude animals;

    @Column(name = "MIN_AGE")
    private Integer minAge;

    @Column(name = "MAX_AGE")
    private Integer maxAge;

    @Column(name = "ROOM_TYPE", columnDefinition = "TEXT")
    @Convert(converter = RoomTypeListConverter.class)
    private List<RoomType> roomType;

    @Column(name = "CONVENIENCES", columnDefinition = "TEXT")
    @Convert(converter = ConvenienceListConverter.class)
    private List<Convenience> conveniences;

    @Column(name = "RENT_PAY")
    private Integer rentPay;

    @Column(name = "DESCRIPTION")
    private String description;

}