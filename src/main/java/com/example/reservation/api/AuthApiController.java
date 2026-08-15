package com.example.reservation.api;

import com.example.reservation.dto.MemberForm;
import com.example.reservation.entity.Member;
import com.example.reservation.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {
	
	private final AuthenticationManager authenticationManager;
	private final MemberService memberService;
	
	private final SecurityContextRepository securityContextRepository =
			new HttpSessionSecurityContextRepository();
	
	// 회원가입
	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public void signup(
			@Valid @RequestBody MemberForm memberForm
	) {
		
		if (!memberForm.getPassword()
				.equals(memberForm.getPasswordConfirm())) {
			
			throw new IllegalArgumentException(
					"비밀번호가 일치하지 않습니다."
			);
		}
		
		memberService.create(memberForm);
	}
	
	// 로그인
	@PostMapping("/login")
	public Map<String, Object> login(
			@RequestBody Map<String, String> request,
			HttpServletRequest httpRequest,
			HttpServletResponse httpResponse
	) {
		
		String email = request.get("email");
		String password = request.get("password");
		
		Authentication authentication =
				authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(
								email,
								password
						)
				);
		
		SecurityContext context =
				SecurityContextHolder.createEmptyContext();
		
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		
		securityContextRepository.saveContext(
				context,
				httpRequest,
				httpResponse
		);
		
		Member member =
				memberService.getMember(authentication.getName());
		
		return Map.of(
				"memberId", member.getId(),
				"name", member.getName(),
				"email", member.getEmail()
		);
	}
	
	@GetMapping("/me")
	public Map<String, Object> me(
			Authentication authentication
	) {
		
		Member member =
				memberService.getMember(authentication.getName());
		
		return Map.of(
				"memberId", member.getId(),
				"name", member.getName(),
				"email", member.getEmail()
		);
	}
	
	// 로그아웃
	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(
			HttpServletRequest request
	) {
		
		if (request.getSession(false) != null) {
			request.getSession(false).invalidate();
		}
		
		SecurityContextHolder.clearContext();
	}
	
	
}
