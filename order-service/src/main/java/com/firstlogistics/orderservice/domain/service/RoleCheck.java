package com.firstlogistics.orderservice.domain.service;

import com.firstlogistics.orderservice.domain.vo.OrderId;

import java.util.UUID;

public interface RoleCheck {
    boolean canRequestCancel(OrderId orderId);
    boolean canAcceptOrCancel(OrderId orderId);
    boolean canView(OrderId orderId, UUID hubId, UUID supplierManagerId, UUID receiverManagerId);
    boolean isMaster();
    boolean isHubManager();
    boolean isCompanyManager();

    UUID getCurrentUserId();
    UUID getCurrentUserHubId();
}
