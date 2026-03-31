package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.application.port.dto.UserResponse;

import java.util.UUID;

public interface UserPort {

	UserResponse getUser(UUID userId);
}
