package com.firstlogistics.userservice.application.service;

import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import com.firstlogistics.userservice.application.dto.result.UserResult;
import com.firstlogistics.userservice.domain.dto.UsersSpec;
import com.firstlogistics.userservice.domain.repository.UserQueryRepository;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService implements com.firstlogistics.userservice.application.port.UserQueryService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;

    public UserResult getUser(UUID userId) {
        return UserResult.fromDomain(userRepository.findByIdNotDeleted(userId));
    }

    public UserResult getMyPage(UUID userId) {
        return UserResult.fromDomain(userRepository.findByIdNotDeleted(userId));
    }

    public Page<UserResult> getUsers(UserGetQuery query, Pageable pageable) {
        Page<UsersSpec> specPage = userQueryRepository.getUsers(query.toSpec(), pageable);

        return specPage.map(UserResult::fromSpec);
    }
}
