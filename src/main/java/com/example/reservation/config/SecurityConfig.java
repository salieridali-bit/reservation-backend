package com.example.reservation.config;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // WebConfig의 CORS 설정 사용
                .cors(cors -> {})

                .authorizeHttpRequests(auth -> auth

                        // 에러 처리 요청 허용
                        .dispatcherTypeMatchers(
                                DispatcherType.ERROR
                        ).permitAll()

                        // 일반 공개 페이지
                        .requestMatchers(
                                "/",
                                "/login",
                                "/signup",
                                "/error",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        // 로그인 없이 사용할 수 있는 인증 API
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login"
                        ).permitAll()

                        // 회의실 조회만 공개
                        .requestMatchers(
                        		HttpMethod.GET,
                                "/api/rooms/**"
                        ).permitAll()

                        // 회의실 등록/수정/삭제는 로그인 필요
                        .requestMatchers(
                               HttpMethod.POST,
                               "/api/rooms/**"
                        ).authenticated()

                        .requestMatchers(
                        		HttpMethod.PUT,
                        		"/api/rooms/**"
                        ).authenticated()
                        
                        .requestMatchers(
                        		HttpMethod.DELETE,
                        		"/api/rooms/**"
                        ).authenticated()
                        
                        // 인증관련
                        .requestMatchers(
                        		"/api/auth/me",
                        		"/api/auth/logout"
                        ).authenticated()
                        
                        // 예약 API는 전부 로그인 필요
                        .requestMatchers(
                        		"/api/reservations/**"
                        ).authenticated()
                        
                        .anyRequest().authenticated()
                )

                // API 요청은 로그인 페이지로 redirect하지 않고
                // 401 Unauthorized 반환
                .exceptionHandling(exception -> exception
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        ),
                                request ->
                                        request.getRequestURI()
                                                .startsWith("/api/")
                        )
                )

                // React REST API는 CSRF 제외
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )

                // 기존 Thymeleaf 로그인도 유지
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )

                // 기존 Thymeleaf 로그아웃도 유지
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }
}
