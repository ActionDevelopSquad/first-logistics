package com.firstlogistics.userservice.infrastructure.persistence.jpa;

import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(User user) {
        return jpaUserRepository.save(UserJpaEntity.from(user)).toDomain();
    }

    @Override
    public User findById(UUID userId) {
        return jpaUserRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND)).toDomain();
    }

    @Override
    public User findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND)).toDomain();
    }
}