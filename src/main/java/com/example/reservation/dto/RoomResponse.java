package com.example.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomResponse {

    private Long id;

    private String name;

    private String description;

    private Integer capacity;
}
