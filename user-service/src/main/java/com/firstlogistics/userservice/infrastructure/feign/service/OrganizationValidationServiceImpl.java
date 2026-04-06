package com.firstlogistics.userservice.infrastructure.feign.service;

import com.firstlogistics.userservice.application.port.OrganizationValidationService;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.infrastructure.feign.client.CompanyClient;
import com.firstlogistics.userservice.infrastructure.feign.client.DeliveryClient;
import com.firstlogistics.userservice.infrastructure.feign.client.HubClient;
import common.response.FeignApiResponse;
import common.security.entity.enums.UserRole;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
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
                throw new UserException(UserErrorCode.HUB_ID_NOT_FOUND);
            }
        } catch (FeignException.NotFound e) {
            throw new UserException(UserErrorCode.HUB_ID_NOT_FOUND);

        } catch (FeignException e) {
            log.error("Feign 호출 실패 - status: {}, message: {}", e.status(), e.getMessage());
            throw new UserException(UserErrorCode.FEIGN_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("조직 검증 중 예외 발생 - type: {}, message: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new UserException(UserErrorCode.FEIGN_SERVICE_UNAVAILABLE);
        }
    }

    private boolean existsOrganization(UUID organizationId, UserRole role) {
        return switch (role) {
            case HUB_MANAGER -> existsHub(organizationId);
            // TODO: 업체 담당자 회원가입 시 업체가 아직 생성되지 않은 상태이므로 검증 스킵.
            //  현재 흐름: 업체 담당자 회원가입 → 업체 생성(managerId 연결)
            //  개선 방향: 회원가입 시 업체를 동기(Feign)로 함께 생성하는 방식 검토 필요
            case COMPANY_MANAGER -> true;
            case DELIVERY_MANAGER -> existsHub(organizationId);
            case MASTER -> true;
        };
    }

    private boolean existsHub(UUID organizationId) {
        FeignApiResponse<?> response = hubClient.existsHub(organizationId);
        return response != null && response.data() != null;
    }

    private boolean existsCompany(UUID organizationId) {
        FeignApiResponse<?> response = companyClient.existsCompany(organizationId);
        return response != null && response.data() != null;
    }

    private boolean existsDelivery(UUID organizationId) {
        FeignApiResponse<?> response = deliveryClient.existsDelivery(organizationId);
        return response != null && response.data() != null;
    }
}