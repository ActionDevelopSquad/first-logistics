package com.firstlogistics.orderservice.domain.vo;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;

public record Address(
        String roadAddress,
        String detailAddress
) {
    public Address {
        if (roadAddress == null || roadAddress.isBlank()) {
            throw new OrderException(OrderErrorCode.DELIVERY_ADDRESS_REQUIRED);
        }
    }

    public static Address of(String roadAddress, String detailAddress) {
        return new Address(roadAddress, detailAddress);
    }

    public String getFullAddress() {
        return String.format("%s %s", roadAddress, detailAddress).trim();
    }
}
