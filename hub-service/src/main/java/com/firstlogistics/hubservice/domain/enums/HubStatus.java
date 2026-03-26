package com.firstlogistics.hubservice.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HubStatus {
    ACTIVE("정상 가동"),
    INACTIVE("운행 중단");


    private final String description;
}
