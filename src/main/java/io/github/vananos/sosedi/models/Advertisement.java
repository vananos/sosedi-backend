package io.github.vananos.sosedi.models;

import lombok.Data;

import javax.persistence.*;

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

    @Column(name="landlord")
    private Boolean landlord;

}