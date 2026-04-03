package com.firstlogistics.userservice.presentation.dto.response;

import com.firstlogistics.userservice.application.dto.result.UserResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserListResponse(
        List<UserResponse> users,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static UserListResponse from(Page<UserResult> page) {

        List<UserResponse> users = page.getContent()
                .stream()
                .map(UserResponse::from)
                .toList();

        return new UserListResponse(
                users,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}