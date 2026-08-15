package com.example.reservation.controller;

import com.example.reservation.dto.RoomForm;
import com.example.reservation.dto.RoomResponse;
import com.example.reservation.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    // 회의실 등록 화면
    @GetMapping("/create")
    public String create(RoomForm roomForm) {
        return "room-create";
    }

    // 회의실 등록
    @PostMapping("/create")
    public String create(
            @Valid RoomForm roomForm,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "room-create";
        }

        roomService.create(roomForm);

        return "redirect:/";
    }

    // 회의실 목록
    @GetMapping("/list")
    public String list(Model model) {

        model.addAttribute("rooms", roomService.findAll());

        return "room-list";
    }

    // 회의실 수정 화면
    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") Long id,
            Model model
    ) {

        RoomResponse room = roomService.findById(id);

        RoomForm form = new RoomForm();

        form.setName(room.getName());
        form.setDescription(room.getDescription());
        form.setCapacity(room.getCapacity());

        model.addAttribute("roomForm", form);
        model.addAttribute("roomId", id);

        return "room-edit";
    }

    // 회의실 수정
    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") Long id,
            @Valid RoomForm roomForm,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roomId", id);
            return "room-edit";
        }

        roomService.update(id, roomForm);

        return "redirect:/room/list";
    }

    // 회의실 삭제
    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            roomService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "회의실이 삭제되었습니다."
            );

        } catch (IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/room/list";
    }
}