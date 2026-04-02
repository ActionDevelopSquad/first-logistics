package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

public record ManagerDetail(String managerName, String phoneNumber) {

	public ManagerDetail {
		if (managerName == null || managerName.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_MANAGER_DETAIL);
		}
		if (phoneNumber == null || phoneNumber.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_MANAGER_DETAIL);
		}
	}

	public static ManagerDetail of(String managerName, String phoneNumber) {
		return new ManagerDetail(managerName, phoneNumber);
	}
}
