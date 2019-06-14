package io.github.vananos.sosedi.models;

import io.github.vananos.sosedi.components.ListConverter;
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

    @Column(name = "MALE")
    private Boolean male;

    @Column(name = "FEMALE")
    private Boolean female;

    @Column(name = "LANDLORD")
    private Boolean landlord;

    @Column(name = "PLACE_ID")
    private String placeId;

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

    @Column(name = "ROOM_TYPE", columnDefinition = "jsonb")
    @Convert(converter = ListConverter.class)
    private List<RoomType> roomType;

    @Column(name = "CONVENIENCES", columnDefinition = "jsonb")
    @Convert(converter = ListConverter.class)
    private List<Convenience> conveniences;

    @Column(name = "RENT_PAY")
    private Integer rentPay;

    @Column(name = "DESCRIPTION")
    private String description;

}