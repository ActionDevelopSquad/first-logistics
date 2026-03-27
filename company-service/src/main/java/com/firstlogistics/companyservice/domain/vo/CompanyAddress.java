package com.firstlogistics.companyservice.domain.vo;

public record CompanyAddress(
        String address
) {

    public static CompanyAddress of(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("주소는 null이거나 빈 문자열일 수 없습니다.");
        }

        return new CompanyAddress(address);
    }
}
