package com.firstlogistics.userservice.domain.repository;

import com.firstlogistics.userservice.domain.entity.User;

import java.util.UUID;

public interface UserRepository {
    User findById(UUID userId);

    User findByIdNotDeleted(UUID userId);

    User findByUsernameNotDeleted(String username);

    User save(User user);

    User update(User user);

    void delete(UUID userId, UUID deletedUserId);
}
