package com.firstlogistics.userservice.domain.entity;

import com.firstlogistics.userservice.domain.enums.Status;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import common.jpa.domain.enums.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private UUID id;
    private String username;
    private String name;
    private String phone;
    private String slackId;
    private Status status;
    private UserRole userRole;
    private LocalDateTime lastLoginAt;

    public static User create(UUID userId, String username, String name, String phone, String slackId, UserRole role) {
        return new User(userId, username, name, phone, slackId, Status.PENDING, role, null);
    }

    public void canLogin() {
        if (this.status != Status.APPROVE) {
            throw new UserException(UserErrorCode.CAN_LOGIN_ONLY_APPROVE);
        }
    }

    public void approve() {
        if (this.status == Status.APPROVE) {
            throw new UserException(UserErrorCode.ALREADY_APPROVE);
        }
        this.status = Status.APPROVE;
    }

    public void reject() {
        if (this.status == Status.REJECTED) {
            throw new UserException(UserErrorCode.ALREADY_REJECTED);
        }
        this.status = Status.REJECTED;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public static User reconstitute(
            UUID id,
            String username,
            String name,
            String phone,
            String slackId,
            Status status,
            UserRole userRole,
            LocalDateTime lastLoginAt
    ) {
        return new User(
                id,
                username,
                name,
                phone,
                slackId,
                status,
                userRole,
                lastLoginAt);
    }
}
