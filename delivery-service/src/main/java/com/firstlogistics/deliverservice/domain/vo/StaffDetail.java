package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

public record StaffDetail(String staffName, String phoneNumber) {

	public StaffDetail {
		if (staffName == null || staffName.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_STAFF_DETAIL);
		}
		if (phoneNumber == null || phoneNumber.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_STAFF_DETAIL);
		}
	}

	public static StaffDetail of(String staffName, String phoneNumber) {
		return new StaffDetail(staffName, phoneNumber);
	}
}
