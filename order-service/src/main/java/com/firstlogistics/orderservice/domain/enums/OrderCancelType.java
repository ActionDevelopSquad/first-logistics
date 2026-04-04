package com.firstlogistics.orderservice.domain.enums;

import lombok.Getter;

@Getter
public enum OrderCancelType {
    ORDERER_REQUEST("발주처 요청 취소"),
    STOCK_OUT("재고 부족"),
    SUPPLIER_CANCEL("공급처 취소"),
    ADMIN_CANCEL("관리자 직권 취소"),
    SYSTEM_ERROR("시스템 오류"),
    ETC("기타 사유");

    private final String description;

    OrderCancelType(String description) {
        this.description = description;
    }
}