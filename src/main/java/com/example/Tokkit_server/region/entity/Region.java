package com.example.Tokkit_server.region.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer sidoCode;

    private String sidoName;

    private Integer sigunguCode;

    private String sigunguName;
}

