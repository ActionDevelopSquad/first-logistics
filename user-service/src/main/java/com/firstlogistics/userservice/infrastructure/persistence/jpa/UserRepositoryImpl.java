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
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        return userMapper.toDomain(jpaUserRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public User findById(UUID userId) {
        return userMapper.toDomain(jpaUserRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public User findByIdNotDeleted(UUID userId) {
        return userMapper.toDomain(jpaUserRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public User findByUsernameNotDeleted(String username) {
        return userMapper.toDomain(jpaUserRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public void delete(UUID userId, UUID deletedUserId) {
        UserJpaEntity entity = getUserEntityForDelete(userId);
        entity.softDelete(deletedUserId);
    }

    private UserJpaEntity getUserEntityForDelete(UUID userId) {
        return jpaUserRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}