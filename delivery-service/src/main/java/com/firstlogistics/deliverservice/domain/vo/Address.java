package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

public record Address(String roadAddress, String detailAddress) {

	public Address {
		if (roadAddress == null || roadAddress.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_ADDRESS);
		}
		if (detailAddress == null || detailAddress.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_ADDRESS);
		}
	}

	public static Address of(String roadAddress, String detailAddress) {
		return new Address(roadAddress, detailAddress);
	}
}
