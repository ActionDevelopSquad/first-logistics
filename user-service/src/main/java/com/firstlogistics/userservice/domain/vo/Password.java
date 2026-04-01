package com.firstlogistics.userservice.domain.vo;

import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record Password(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/]).{8,15}$",
                message = "비밀번호는 8 ~ 15자 사이의 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String confirmPassword
)
{
    public Password {
        if (!password.equals(confirmPassword)) {
            throw new UserException(UserErrorCode.ID_PASSWORD_NOT_MATCH);
        }
    }

    public String getPassword() {
        return password;
    }
}
