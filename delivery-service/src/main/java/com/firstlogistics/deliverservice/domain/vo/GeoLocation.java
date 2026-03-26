package com.firstlogistics.deliverservice.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GeoLocation {

	private double latitude;
	private double longitude;

	public static GeoLocation of(double latitude, double longitude) {
		GeoLocation geoLocation = new GeoLocation();
		geoLocation.latitude = latitude;
		geoLocation.longitude = longitude;
		return geoLocation;
	}
}
