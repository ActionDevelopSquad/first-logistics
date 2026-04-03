package com.firstlogistics.userservice.infrastructure.feign.service;

import com.firstlogistics.userservice.application.port.OrganizationValidationService;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.infrastructure.feign.client.CompanyClient;
import com.firstlogistics.userservice.infrastructure.feign.client.DeliveryClient;
import com.firstlogistics.userservice.infrastructure.feign.client.HubClient;
import common.jpa.entity.enums.UserRole;
import common.response.ApiResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationValidationServiceImpl implements OrganizationValidationService {

    private final HubClient hubClient;
    private final CompanyClient companyClient;
    private final DeliveryClient deliveryClient;

    @Override
    public void validateOrganizationExists(UUID organizationId, UserRole role) {
        try {
            if (!existsOrganization(organizationId, role)) {
                throw new UserException(UserErrorCode.ORGANIZATION_ID_NOT_FOUND);
            }
        } catch (FeignException.NotFound e) {
            throw new UserException(UserErrorCode.ORGANIZATION_ID_NOT_FOUND);

        } catch (FeignException e) {
            throw new UserException(UserErrorCode.FEIGN_SERVICE_UNAVAILABLE);
        }
    }

    private boolean existsOrganization(UUID organizationId, UserRole role) {
        return switch (role) {
            case HUB_MANAGER -> existsHub(organizationId);
            case COMPANY_MANAGER -> existsCompany(organizationId);
            case DELIVERY_MANAGER -> existsDelivery(organizationId);
            case MASTER -> true;
        };
    }

    private boolean existsHub(UUID organizationId) {
        ApiResponse<UUID> response = hubClient.existsHub(organizationId);
        return response != null && response.getData() != null;
    }

    private boolean existsCompany(UUID organizationId) {
        ApiResponse<UUID> response = companyClient.existsCompany(organizationId);
        return response != null && response.getData() != null;
    }

    private boolean existsDelivery(UUID organizationId) {
        ApiResponse<UUID> response = deliveryClient.existsDelivery(organizationId);
        return response != null && response.getData() != null;
    }
}