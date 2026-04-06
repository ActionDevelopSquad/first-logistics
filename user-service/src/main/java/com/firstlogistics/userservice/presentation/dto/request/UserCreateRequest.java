package com.firstlogistics.userservice.presentation.dto.request;

import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.domain.enums.ManagerType;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.domain.vo.Password;
import common.security.entity.enums.UserRole;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record UserCreateRequest(
        @NotBlank(message = "아이디를 입력해주세요.")
        @Pattern(
                regexp = "^[a-z0-9]{4,10}$",
                message = "아이디는 4 ~ 10자 사이의 소문자와 숫자로 구성되어야 합니다."
        )
        String username,

        Password password,

        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 20)
        String firstName,

        @NotBlank(message = "성을 입력해주세요.")
        @Size(max = 10)
        String lastName,

        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(
                regexp = "^(01[016789])-?\\d{3,4}-?\\d{4}$",
                message = "올바른 휴대폰 번호 형식이 아닙니다."
        )
        String phone,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email
        String email,

        @NotBlank(message = "슬랙 아이디를 입력해주세요.")
        String slackId,

        @NotNull(message = "권한을 입력해주세요.")
        UserRole userRole,

        @NotNull(message = "허브 아이디를 입력해주세요.")
        UUID hubId,

        ManagerType managerType
)
{
        public UserCreateRequest {
                if (userRole == UserRole.DELIVERY_MANAGER && managerType == null) {
                        throw new UserException(UserErrorCode.MANAGER_TYPE_REQUIRED);
                }

                if (userRole != UserRole.DELIVERY_MANAGER && managerType != null) {
                        throw new UserException(UserErrorCode.MANAGER_TYPE_NOT_ALLOWED);
                }
        }

        public UserCreateCommand toCommand() {
                return new UserCreateCommand(
                        username.trim(),
                        password.getPassword(),
                        firstName.trim(),
                        lastName.trim(),
                        phone.trim(),
                        email.trim(),
                        slackId.trim(),
                        userRole,
                        hubId,
                        managerType
                );
        }
}
