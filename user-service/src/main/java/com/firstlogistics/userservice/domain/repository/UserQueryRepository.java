package com.firstlogistics.userservice.domain.repository;

import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import com.firstlogistics.userservice.domain.dto.UsersSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {
    Page<UsersSpec> getUsers(UserGetQuery spec, Pageable pageable);
}
