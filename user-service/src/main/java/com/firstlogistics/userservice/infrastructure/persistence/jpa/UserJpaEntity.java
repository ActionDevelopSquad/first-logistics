package com.firstlogistics.userservice.infrastructure.persistence.jpa;

import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.enums.Status;
import common.jpa.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 10)
    private String username;

//    @Column(name = "password", nullable = false, length = 15)
//    private String password;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 30)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private Status status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "slack_id", length = 100)
    private String slackId;

    // Domain → Entity 변환
    public static UserJpaEntity from(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getPhone(),
                user.getUserRole(),
                user.getStatus(),
                user.getLastLoginAt(),
                user.getSlackId()
        );
    }

    // Entity → Domain 변환
    public User toDomain() {
        return User.reconstitute(id, username, name, phone, slackId, status, userRole, lastLoginAt);
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}