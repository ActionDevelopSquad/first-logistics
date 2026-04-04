package com.firstlogistics.userservice.presentation.dto.request;

import com.firstlogistics.userservice.application.dto.command.UserUpdateCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserUpdateRequest(
        @Size(max = 20)
        String firstName,

        @Size(max = 10)
        String lastName,

        @Email
        String email,

        @Pattern(
                regexp = "^(01[016789])-?\\d{3,4}-?\\d{4}$",
                message = "올바른 휴대폰 번호 형식이 아닙니다."
        )
        String phone,

        String slackId
)
{
        public UserUpdateCommand toCommand(UUID userId) {
                return new UserUpdateCommand(
                        userId,
                        firstName,
                        lastName,
                        email,
                        phone,
                        slackId
                );
        }
}
