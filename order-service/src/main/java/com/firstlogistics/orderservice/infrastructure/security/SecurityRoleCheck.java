package com.firstlogistics.orderservice.infrastructure.security;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import com.firstlogistics.orderservice.infrastructure.feign.HubClient;
import com.firstlogistics.orderservice.application.port.dto.HubManagerResponse;
import common.response.ApiResponse;
import common.security.entity.enums.UserRole;
import common.security.security.util.SecurityUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityRoleCheck implements RoleCheck {

    private final OrderRepository orderRepository;
    private final HubClient hubClient;

    @Override
    public boolean canRequestCancel(OrderId orderId) {
        return isReceiverOf(orderId);
    }

    @Override
    public boolean canAcceptOrCancel(OrderId orderId) {
        return isMaster() ||
                isHubManagerOf(orderId) ||
                isSupplierOf(orderId);
    }

    @Override
    public boolean canView(OrderId orderId, UUID hubId, UUID supplierManagerId, UUID receiverManagerId) {
        if (isMaster()) return true;

        if (isHubManager()) {
            UUID myHubId = getCurrentUserHubId();
            return hubId.equals(myHubId);
        }

        if (isCompanyManager()) {
            UUID userId = getCurrentUserId();
            return supplierManagerId.equals(userId) ||
                    receiverManagerId.equals(userId);
        }

        return false;
    }

    private boolean hasRole(UserRole role) {
        UserRole currentRole = SecurityUtils.currentUser().getRole();
        return currentRole != null && currentRole == role;
    }

    @Override
    public boolean isMaster() {
        return hasRole(UserRole.MASTER);
    }

    @Override
    public boolean isHubManager() {
        return hasRole(UserRole.HUB_MANAGER);
    }

    @Override
    public boolean isCompanyManager() {
        return hasRole(UserRole.COMPANY_MANAGER);
    }

    @Override
    public UUID getCurrentUserId() {
        try {
            return SecurityUtils.currentUser().getUserId();
        } catch (Exception e) {
            log.warn("권한 확인 중 인증 예외 발생: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public UUID getCurrentUserHubId() {
        if (!isHubManager()) return null;

        UUID userId = getCurrentUserId();
        if (userId == null) return null;

        try {
            ApiResponse<HubManagerResponse> response = hubClient.getHubManagerInfo(userId);
            if (response != null && response.getData() != null) {
                return response.getData().hubId();
            }
        } catch (FeignException.NotFound e) {
            return null;
        } catch (Exception e) {
            log.error("허브 서비스 호출 중 시스템 에러 발생: {}", e.getMessage());
            throw new OrderException(OrderErrorCode.EXTERNAL_SERVICE_ERROR);
        }
        return null;
    }

    private boolean isHubManagerOf(OrderId orderId) {
        if (hasRole(UserRole.HUB_MANAGER)) {
            UUID hubId = getCurrentUserHubId();
            return orderRepository.existsByIdAndSupplierHubId(orderId, hubId);
        }
        return false;
    }

    private boolean isSupplierOf(OrderId orderId) {
        if (hasRole(UserRole.COMPANY_MANAGER)) {
            return orderRepository.existsByIdAndSupplierManagerId(orderId, getCurrentUserId());
        }
        return false;
    }

    private boolean isReceiverOf(OrderId orderId) {
        if (hasRole(UserRole.COMPANY_MANAGER)) {
            return orderRepository.existsByIdAndReceiverManagerId(orderId, getCurrentUserId());
        }
        return false;
    }

}
