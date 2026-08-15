package com.example.reservation.api;

import com.example.reservation.dto.RoomForm;
import com.example.reservation.dto.RoomResponse;
import com.example.reservation.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomApiController {

    private final RoomService roomService;

    @GetMapping
    public List<RoomResponse> list() {
        return roomService.findAll();
    }

    @GetMapping("/{id}")
    public RoomResponse detail(
            @PathVariable("id") Long id
    ) {
        return roomService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(
            @Valid @RequestBody RoomForm roomForm
    ) {
        roomService.create(roomForm);
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoomForm roomForm
    ) {
        roomService.update(id, roomForm);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") Long id
    ) {
        roomService.delete(id);
    }
}