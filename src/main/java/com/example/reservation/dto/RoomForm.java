package com.example.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomForm {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Integer capacity;
}
