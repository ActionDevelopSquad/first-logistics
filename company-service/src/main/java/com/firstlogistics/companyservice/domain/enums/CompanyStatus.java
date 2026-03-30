package com.firstlogistics.companyservice.domain.enums;

public enum CompanyStatus {
    ACTIVE("활성화"),
    INACTIVE("비활성화");

    private final String description;

    CompanyStatus(String description) {
        this.description = description;
    }
}
