package com.firstlogistics.companyservice.domain.vo;

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
            throw new IllegalArgumentException("도로명 주소는 null이거나 빈 문자열일 수 없습니다.");
        }

        if (detailAddress == null || detailAddress.isBlank()) {
            throw new IllegalArgumentException("상세 주소는 null이거나 빈 문자열일 수 없습니다.");
        }

        if (roadAddress.length() > 255 ||  detailAddress.length() > 255) {
            throw new IllegalArgumentException("도로명 주소와 상세 주소는 255자를 초과할 수 없습니다.");
        }
    }
}
