package com.firstlogistics.orderservice.application.port;

import common.security.entity.enums.UserRole;

import java.util.UUID;

public interface UserContextPort {
    UUID getCurrentUserId();

    UserRole getCurrentUserRole();

    boolean isMaster();

    boolean isHubManager();

    boolean isCompanyManager();
}