package com.firstlogistics.hubservice.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HubConnectionStatus {
    ACTIVE("연결 활성화"),
    INACTIVE("연결 비활성화");


    private final String description;
}
