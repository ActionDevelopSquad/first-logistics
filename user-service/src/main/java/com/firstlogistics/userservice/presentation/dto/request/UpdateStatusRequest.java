package com.firstlogistics.userservice.presentation.dto.request;

import com.firstlogistics.userservice.domain.enums.Status;

public record UpdateStatusRequest(
        Status status
)
{}