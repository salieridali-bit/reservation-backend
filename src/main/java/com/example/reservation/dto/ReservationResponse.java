package com.example.reservation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ReservationResponse {

    private Long id;

    private Long roomId;

    private String roomName;

    private LocalDate reservationDate;

    private LocalTime startTime;

    private LocalTime endTime;
}
