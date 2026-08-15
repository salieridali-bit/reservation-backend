package com.example.reservation.controller;

import com.example.reservation.dto.ReservationForm;
import com.example.reservation.dto.ReservationResponse;
import com.example.reservation.security.MemberUserDetails;
import com.example.reservation.service.ReservationService;
import com.example.reservation.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomService roomService;

    // 예약 생성 화면
    @GetMapping("/create")
    public String create(
            ReservationForm reservationForm,
            Model model
    ) {
        model.addAttribute("rooms", roomService.findAll());
        return "reservation-create";
    }

    // 예약 생성
    @PostMapping("/create")
    public String create(
            @Valid ReservationForm reservationForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal MemberUserDetails userDetails,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", roomService.findAll());
            return "reservation-create";
        }

        try {
            reservationService.create(
                    userDetails.getMemberId(),
                    reservationForm
            );
        } catch (IllegalArgumentException e) {
            bindingResult.reject(
                    "reservationError",
                    e.getMessage()
            );

            model.addAttribute("rooms", roomService.findAll());
            return "reservation-create";
        }

        return "redirect:/reservation/list";
    }

    // 내 예약 목록
    @GetMapping("/list")
    public String list(
            @AuthenticationPrincipal MemberUserDetails userDetails,
            Model model
    ) {

        model.addAttribute(
                "reservations",
                reservationService.findByMemberId(
                        userDetails.getMemberId()
                )
        );

        return "reservation-list";
    }

    // 예약 취소
    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {

        reservationService.delete(
                id,
                userDetails.getMemberId()
        );

        return "redirect:/reservation/list";
    }

    // 예약 수정 화면
    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails,
            Model model
    ) {

        ReservationResponse reservation =
                reservationService.findById(
                        id,
                        userDetails.getMemberId()
                );

        ReservationForm form = new ReservationForm();

        form.setRoomId(reservation.getRoomId());
        form.setReservationDate(reservation.getReservationDate());
        form.setStartTime(reservation.getStartTime());
        form.setEndTime(reservation.getEndTime());

        model.addAttribute("reservationForm", form);
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("reservationId", id);

        return "reservation-edit";
    }

    // 예약 수정
    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") Long id,
            @Valid ReservationForm reservationForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal MemberUserDetails userDetails,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("reservationId", id);

            return "reservation-edit";
        }

        try {
            reservationService.update(
                    id,
                    userDetails.getMemberId(),
                    reservationForm
            );
        } catch (IllegalArgumentException e) {

            bindingResult.reject(
                    "reservationError",
                    e.getMessage()
            );

            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("reservationId", id);

            return "reservation-edit";
        }

        return "redirect:/reservation/list";
    }
}
