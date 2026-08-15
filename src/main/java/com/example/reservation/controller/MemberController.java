package com.example.reservation.controller;

import com.example.reservation.dto.MemberForm;
import com.example.reservation.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String singup(MemberForm memberForm) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid MemberForm memberForm,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        if (!memberForm.getPassword().equals(memberForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "passwordMismatch",
                    "비밀번호가 일치하지 않습니다.");
            return "signup";
        }

        memberService.create(memberForm);
        return "redirect:/login";
    }
}
