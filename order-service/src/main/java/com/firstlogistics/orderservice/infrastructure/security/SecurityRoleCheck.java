package com.firstlogistics.orderservice.infrastructure.security;

import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import com.firstlogistics.orderservice.infrastructure.feign.HubClient;
import com.firstlogistics.orderservice.infrastructure.feign.dto.HubManagerResponse;
import common.response.ApiResponse;
import common.security.entity.enums.UserRole;
import common.security.security.domain.CustomUserDetails;
import common.security.security.util.SecurityUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityRoleCheck implements RoleCheck {

    private final OrderRepository orderRepository;
    private final HubClient hubClient;

    @Override
    public boolean hasRole(UserRole role) {
        return hasRole(List.of(role));
    }

    @Override
    public boolean hasRole(List<UserRole> roles) {
        if (roles == null || roles.isEmpty()) return false;

        return Optional.ofNullable(SecurityUtils.currentUser())
                .map(userDetails -> roles.contains(userDetails.getRole()))
                .orElse(false);
    }

    @Override
    public boolean hasOrderAuthority(OrderId orderId) { // 이름 변경 추천
        CustomUserDetails user = SecurityUtils.currentUser();
        UUID userId = user.getUserId();
        UserRole role = user.getRole();

        if (role == UserRole.MASTER) return true;

        return switch (role) {
            // 업체 관리자: 공급 업체 or 수령 업체 담당자면 접근 가능
            case UserRole.COMPANY_MANAGER ->
                    orderRepository.existsByIdAndSupplierManagerId(orderId, userId) ||
                    orderRepository.existsByIdAndReceiverManagerId(orderId, userId);

            // 허브 관리자: 주문의 hubId와 본인의 허브 ID 비교
            case UserRole.HUB_MANAGER -> {
                try {
                    // 허브 서비스에서 본인의 소속 허브 ID를 가져와서 비교
                    ApiResponse<HubManagerResponse> response = hubClient.getHubManagerInfo(userId);

                    if (response != null && response.getStatus().is2xxSuccessful() && response.getData() != null) {
                        UUID hubId = response.getData().hubId();
                        yield orderRepository.existsByIdAndSupplierHubId(orderId, hubId);
                    }
                    yield false;
                } catch (FeignException.NotFound e) {
                    log.info("허브 관리자 정보를 찾을 수 없습니다. userId: {}", userId);
                    yield false;
                } catch (Exception e) {
                    log.error("허브 권한 검증 중 외부 서비스 오류 발생: {}", e.getMessage());
                    yield false;
                }
            }

            default -> false;
        };
    }
}
