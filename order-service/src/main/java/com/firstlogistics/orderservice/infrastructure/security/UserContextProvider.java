package com.firstlogistics.orderservice.infrastructure.security;

import com.firstlogistics.orderservice.application.port.UserContextPort;
import common.security.entity.enums.UserRole;
import common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserContextProvider implements UserContextPort {

    @Override
    public UUID getCurrentUserId() {
        return SecurityUtils.currentUser().getUserId();
    }

    @Override
    public UserRole getCurrentUserRole() {
        return SecurityUtils.currentUser().getRole();
    }

    @Override
    public boolean isMaster() {
        return getCurrentUserRole() == UserRole.MASTER;
    }

    @Override
    public boolean isHubManager() {
        return getCurrentUserRole() == UserRole.HUB_MANAGER;
    }

    @Override
    public boolean isCompanyManager() {
        return getCurrentUserRole() == UserRole.COMPANY_MANAGER;
    }
}