package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

public record Time(int minutes) {

	public Time {
		if (minutes < 0) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_TIME);
		}
	}

	public static Time of(int minutes) {
		return new Time(minutes);
	}
}
