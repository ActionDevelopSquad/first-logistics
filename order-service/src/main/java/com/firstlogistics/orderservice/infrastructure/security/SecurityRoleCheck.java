package com.firstlogistics.orderservice.infrastructure.security;

import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import com.firstlogistics.orderservice.infrastructure.feign.HubClient;
import com.firstlogistics.orderservice.infrastructure.feign.dto.HubManagerResponse;
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
    public boolean hasRole(UserRole role) {
        return SecurityUtils.currentUser().getRole() == role;
    }

    @Override
    public boolean isMaster() {
        return hasRole(UserRole.MASTER);
    }

    @Override
    public boolean isHubManagerOf(OrderId orderId) {
        if (hasRole(UserRole.HUB_MANAGER)) {
            UUID userId = SecurityUtils.currentUser().getUserId();

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

    @Override
    public boolean isSupplierOf(OrderId orderId) {
        UUID userId = SecurityUtils.currentUser().getUserId();
        if (hasRole(UserRole.COMPANY_MANAGER)) {
            return orderRepository.existsByIdAndSupplierManagerId(orderId, userId);
        }
        return false;
    }

    @Override
    public boolean isReceiverOf(OrderId orderId) {
        UUID userId = SecurityUtils.currentUser().getUserId();
        if (hasRole(UserRole.COMPANY_MANAGER)) {
            return orderRepository.existsByIdAndReceiverManagerId(orderId, userId);
        }
        return false;
    }

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
}
