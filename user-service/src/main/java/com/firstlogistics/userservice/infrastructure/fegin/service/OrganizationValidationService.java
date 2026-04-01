package com.firstlogistics.userservice.infrastructure.fegin.service;

import common.jpa.entity.enums.UserRole;

import java.util.UUID;

public interface OrganizationValidationService {
    void validateOrganizationExists(UUID organizationId, UserRole role);
}