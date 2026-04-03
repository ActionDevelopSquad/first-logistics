package com.firstlogistics.userservice.presentation.dto.request;

import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import common.jpa.entity.enums.UserRole;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsersGetRequest(
        String username,
        String name,
        String phone,
        UserRole userRole,
        String slackId,
        UUID organizationId,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime lastLoginAt,

        Integer page,
        Integer size
)
{
    public UserGetQuery toQuery() {
        return new UserGetQuery(
                trimToNull(username),
                trimToNull(name),
                trimToNull(phone),
                userRole,
                trimToNull(slackId),
                organizationId,
                lastLoginAt,
                normalizePage(page),
                normalizeSize(size)
        );
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static int normalizePage(Integer page) {
        if (page == null || page < 0) {
            return 0;
        }
        return page;
    }

    private static int normalizeSize(Integer size) {
        if (size == null) {
            return 10;
        }
        return switch (size) {
            case 10, 30, 50 -> size;
            default -> 10;
        };
    }
}
