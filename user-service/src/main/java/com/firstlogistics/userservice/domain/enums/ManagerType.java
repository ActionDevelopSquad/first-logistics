package com.firstlogistics.userservice.domain.enums;

public enum ManagerType {
    HUB_DELIVERY("허브 배송 담당자"),
    COMPANY_DELIVERY("업체 배송 담당자");

    private final String description;

    ManagerType(String description) {
        this.description = description;
    }
}