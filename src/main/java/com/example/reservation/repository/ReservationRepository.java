package com.example.reservation.repository;

import com.example.reservation.entity.Reservation;
import com.example.reservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByRoomIdAndReservationDate(
            Long roomId,
            LocalDate reservationDate
    );

    boolean existsByRoom(Room room);
}
