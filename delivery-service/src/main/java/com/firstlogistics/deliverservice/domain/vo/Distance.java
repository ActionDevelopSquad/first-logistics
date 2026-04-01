package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

public record Distance(int meters) {

	public Distance {
		if (meters < 0) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DISTANCE);
		}
	}

	public static Distance of(int meters) {
		return new Distance(meters);
	}
}
