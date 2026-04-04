package com.firstlogistics.orderservice.infrastructure.security;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import common.security.entity.enums.UserRole;
import common.security.security.domain.CustomUserDetails;
import common.security.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityRoleCheck implements RoleCheck {

    private final OrderRepository orderRepository;

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
                // 허브 서비스에서 본인의 소속 허브 ID를 가져와서 비교
                UUID managedHubId = UUID.randomUUID(); // TODO: FeignClient 연결해서 구현
                yield orderRepository.existsByIdAndSupplierHubId(orderId, managedHubId);
            }

            default -> false;
        };
    }
}
