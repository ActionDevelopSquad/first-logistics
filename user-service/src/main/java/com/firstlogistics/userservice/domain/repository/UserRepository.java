package com.firstlogistics.userservice.domain.repository;

import com.firstlogistics.userservice.domain.entity.User;

import java.util.UUID;

public interface UserRepository {
    User save(User user);

    User findById(UUID userId);

    User findByIdNotDeleted(UUID userId);

    User findByUsernameNotDeleted(String username);

    void delete(UUID userId, UUID deletedUserId);
}
