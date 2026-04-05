package com.firstlogistics.orderservice.domain.service;

import com.firstlogistics.orderservice.domain.vo.OrderId;

import java.util.UUID;

public interface RoleCheck {

    boolean canRequestCancel(OrderId orderId);

    boolean canAcceptOrCancel(OrderId orderId, UUID supplierHubId);

    boolean canView(OrderId orderId, UUID supplierHubId, UUID supplierManagerId, UUID receiverManagerId, UUID myHubId, UUID myUserId);

    boolean canDelete(OrderId orderId, UUID supplierHubId, UUID myHubId);
}
