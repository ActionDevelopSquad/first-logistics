package com.firstlogistics.userservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "아이디를 입력해주세요.")
        @Pattern(
                regexp = "^[a-z0-9]{4,10}$",
                message = "아이디 형식이 올바르지 않습니다."
        )
        String username,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/]).{8,15}$",
                message = "비밀번호 형식이 올바르지 않습니다."
        )
        String password
) {}