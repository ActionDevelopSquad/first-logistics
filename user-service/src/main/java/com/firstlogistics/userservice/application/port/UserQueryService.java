package com.firstlogistics.userservice.application.port;

import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import com.firstlogistics.userservice.application.dto.result.UserResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserQueryService {
    UserResult getUser(UUID userId);

    UserResult getMyPage(UUID userId);

    Page<UserResult> getUsers(UserGetQuery query, Pageable pageable);
}
