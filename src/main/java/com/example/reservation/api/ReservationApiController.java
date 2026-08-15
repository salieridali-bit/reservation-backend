package com.example.reservation.api;

import com.example.reservation.dto.ReservationForm;
import com.example.reservation.dto.ReservationResponse;
import com.example.reservation.security.MemberUserDetails;
import com.example.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationApiController {

    private final ReservationService reservationService;

    // 로그인한 사용자의 예약 목록
    @GetMapping
    public List<ReservationResponse> list(
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        return reservationService.findByMemberId(
                userDetails.getMemberId()
        );
    }

    // 로그인한 사용자의 예약 상세
    @GetMapping("/{id}")
    public ReservationResponse detail(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        return reservationService.findById(
                id,
                userDetails.getMemberId()
        );
    }

    // 예약 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(
            @AuthenticationPrincipal MemberUserDetails userDetails,
            @Valid @RequestBody ReservationForm form
    ) {
        reservationService.create(
                userDetails.getMemberId(),
                form
        );
    }

    // 예약 수정
    @PutMapping("/{id}")
    public void update(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails,
            @Valid @RequestBody ReservationForm form
    ) {
        reservationService.update(
                id,
                userDetails.getMemberId(),
                form
        );
    }

    // 예약 취소
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        reservationService.delete(
                id,
                userDetails.getMemberId()
        );
    }
}