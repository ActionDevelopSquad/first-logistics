package com.firstlogistics.userservice.presentation.dto.request;

import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import com.firstlogistics.userservice.domain.enums.Status;
import common.jpa.entity.enums.UserRole;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsersGetRequest(
        String username,
        String name,
        String phone,
        UserRole userRole,
        Status status,
        String slackId,
        UUID organizationId,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime lastLoginAt
)
{
    public UserGetQuery toQuery() {
        return new UserGetQuery(
                trimToNull(username),
                trimToNull(name),
                trimToNull(phone),
                userRole,
                status,
                trimToNull(slackId),
                organizationId,
                lastLoginAt
        );
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
