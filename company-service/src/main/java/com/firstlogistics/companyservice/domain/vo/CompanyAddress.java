package com.firstlogistics.companyservice.domain.vo;

import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;

public record CompanyAddress(
        String roadAddress,
        String detailAddress
) {
    public CompanyAddress {
        validateAddress(roadAddress, detailAddress);
    }

    public static CompanyAddress of(String roadAddress, String detailAddress) {
        return new CompanyAddress(roadAddress, detailAddress);
    }

    private void validateAddress(String roadAddress, String detailAddress) {
        if (roadAddress == null || roadAddress.isBlank()) {
            throw new CompanyException(CompanyErrorCode.INVALID_ADDRESS);
        }

        if (detailAddress == null || detailAddress.isBlank()) {
            throw new CompanyException(CompanyErrorCode.INVALID_ADDRESS);
        }

        if (roadAddress.length() > 255 || detailAddress.length() > 255) {
            throw new CompanyException(CompanyErrorCode.INVALID_ADDRESS);
        }
    }
}
