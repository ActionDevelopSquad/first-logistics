package com.firstlogistics.orderservice.domain.service;

import com.firstlogistics.orderservice.domain.vo.OrderId;
import common.security.entity.enums.UserRole;

public interface RoleCheck {
    boolean hasRole(UserRole role);

    boolean isMaster();
    boolean isHubManagerOf(OrderId orderId);
    boolean isSupplierOf(OrderId orderId);
    boolean isReceiverOf(OrderId orderId);

    boolean canRequestCancel(OrderId orderId);
    boolean canAcceptOrCancel(OrderId orderId);
}
