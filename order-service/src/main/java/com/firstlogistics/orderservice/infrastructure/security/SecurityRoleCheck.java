package com.firstlogistics.orderservice.infrastructure.security;

import com.firstlogistics.orderservice.application.port.UserContextPort;
import com.firstlogistics.orderservice.domain.repository.OrderRepository;
import com.firstlogistics.orderservice.domain.service.RoleCheck;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityRoleCheck implements RoleCheck {

    private final OrderRepository orderRepository;
    private final UserContextPort userContext;

    @Override
    public boolean canRequestCancel(OrderId orderId) {
        if (!userContext.isCompanyManager()) return false;
        return orderRepository.existsByIdAndReceiverManagerId(orderId, userContext.getCurrentUserId());
    }

    @Override
    public boolean canAcceptOrCancel(OrderId orderId, UUID supplierHubId) {
        if (userContext.isMaster()) return true;
        if (userContext.isHubManager()) {
            return orderRepository.existsByIdAndSupplierHubId(orderId, supplierHubId);
        }
        if (userContext.isCompanyManager()) {
            return orderRepository.existsByIdAndSupplierManagerId(orderId, userContext.getCurrentUserId());
        }
        return false;
    }

    @Override
    public boolean canView(OrderId orderId, UUID supplierHubId, UUID supplierManagerId, UUID receiverManagerId, UUID myHubId, UUID myUserId) {
        if (userContext.isMaster()) return true;

        if (userContext.isHubManager()) {
            return supplierHubId.equals(myHubId);
        }

        if (userContext.isCompanyManager()) {
            return myUserId.equals(supplierManagerId) || myUserId.equals(receiverManagerId);
        }

        return false;
    }

    @Override
    public boolean canDelete(OrderId orderId, UUID supplierHubId, UUID myHubId) {
        if (userContext.isMaster()) return true;

        if (userContext.isHubManager()) {
            return supplierHubId.equals(myHubId);
        }

        return false;
    }
}