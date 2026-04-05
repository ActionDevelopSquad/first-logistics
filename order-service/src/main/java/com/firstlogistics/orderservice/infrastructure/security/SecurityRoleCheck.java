package com.firstlogistics.orderservice.infrastructure.security;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import com.firstlogistics.orderservice.infrastructure.feign.HubClient;
import com.firstlogistics.orderservice.application.port.dto.HubManagerResponse;
import common.response.ApiResponse;
import common.security.entity.enums.UserRole;
import common.security.security.util.SecurityUtils;
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
        try {
            UserRole currentRole = SecurityUtils.currentUser().getRole();
            return currentRole != null && currentRole == role;
        } catch (Exception e) {
            log.warn("권한 확인 중 인증 예외 발생: {}", e.getMessage());
            return false;
        }
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

        try {
            ApiResponse<HubManagerResponse> response = hubClient.getHubManagerInfo(getCurrentUserId());
            if (response != null && response.getData() != null) {
                return response.getData().hubId();
            }
        } catch (Exception e) {
            log.error("허브 정보 조회 실패: {}", e.getMessage());
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
