package com.firstlogistics.userservice.application.port;

import common.security.entity.enums.UserRole;

import java.util.UUID;

public interface OrganizationValidationService {
    void validateOrganizationExists(UUID organizationId, UserRole role);
}