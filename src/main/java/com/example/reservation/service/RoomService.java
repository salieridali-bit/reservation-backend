package com.example.reservation.service;

import com.example.reservation.dto.RoomForm;
import com.example.reservation.dto.RoomResponse;
import com.example.reservation.entity.Room;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public void create(RoomForm form) {
        Room room = Room.builder()
                .name(form.getName())
                .description(form.getDescription())
                .capacity(form.getCapacity())
                .build();

        roomRepository.save(room);
    }

    public List<RoomResponse> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public RoomResponse mapToResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .capacity(room.getCapacity())
                .build();
    }
    public RoomResponse findById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow();

        return mapToResponse(room);

    }

    public void update(Long id, RoomForm form) {
        Room room = roomRepository.findById(id)
                .orElseThrow();

        room.setName(form.getName());
        room.setDescription(form.getDescription());
        room.setCapacity(form.getCapacity());

        roomRepository.save(room);
    }

    public void delete(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow();

        if (reservationRepository.existsByRoom(room)) {
            throw new IllegalStateException("예약이 존재하는 회의실은 삭제할 수 없습니다.");
        }

        roomRepository.delete(room);
    }
}
