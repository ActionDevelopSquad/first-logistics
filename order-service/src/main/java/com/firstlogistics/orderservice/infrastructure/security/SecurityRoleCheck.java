package com.firstlogistics.orderservice.infrastructure.security;

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

    private boolean hasRole(UserRole role) {
        UserRole currentRole = getCurrentUserRole();
        return currentRole != null && currentRole == role;
    }

    private boolean isMaster() {
        return hasRole(UserRole.MASTER);
    }

    private boolean isHubManagerOf(OrderId orderId) {
        if (hasRole(UserRole.HUB_MANAGER)) {
            UUID userId = getCurrentUserId();

            try {
                // 허브 서비스에서 본인의 소속 허브 ID를 가져와서 비교
                ApiResponse<HubManagerResponse> response = hubClient.getHubManagerInfo(userId);

                if (response != null && response.getStatus().is2xxSuccessful() && response.getData() != null) {
                    UUID hubId = response.getData().hubId();
                    return orderRepository.existsByIdAndSupplierHubId(orderId, hubId);
                }
                return false;
            } catch (FeignException.NotFound e) {
                log.info("허브 관리자 정보를 찾을 수 없습니다. userId: {}", userId);
                return false;
            } catch (Exception e) {
                log.error("허브 권한 검증 중 외부 서비스 오류 발생: {}", e.getMessage());
                return false;
            }
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

    private UUID getCurrentUserId() {
        try {
            return SecurityUtils.currentUser().getUserId();
        } catch (Exception e) {
            log.warn("권한 확인 중 인증 예외 발생: {}", e.getMessage());
            return null;
        }
    }

    private UserRole getCurrentUserRole() {
        try {
            return SecurityUtils.currentUser().getRole();
        } catch (Exception e) {
            log.warn("권한 확인 중 인증 예외 발생: {}", e.getMessage());
            return null;
        }
    }
}
