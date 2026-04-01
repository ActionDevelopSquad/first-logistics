package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

public record GeoLocation(double latitude, double longitude) {

	public GeoLocation {
		if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_GEO_LOCATION);
		}
		if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_GEO_LOCATION);
		}
	}

	public static GeoLocation of(double latitude, double longitude) {
		return new GeoLocation(latitude, longitude);
	}
}
