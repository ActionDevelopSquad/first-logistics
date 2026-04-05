package com.firstlogistics.orderservice.domain.service;

import com.firstlogistics.orderservice.domain.vo.OrderId;

public interface RoleCheck {
    boolean canRequestCancel(OrderId orderId);
    boolean canAcceptOrCancel(OrderId orderId);
}
