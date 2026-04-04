package com.firstlogistics.orderservice.domain.service;

import com.firstlogistics.orderservice.domain.vo.OrderId;
import common.security.entity.enums.UserRole;

import java.util.List;

public interface RoleCheck {
    boolean hasRole(UserRole role);
    boolean hasRole(List<UserRole> roles);
    boolean hasOrderAuthority(OrderId id);
}
