package com.firstlogistics.deliverservice.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Distance {

	private int meters;

	public static Distance of(int meters) {
		Distance distance = new Distance();
		distance.meters = meters;
		return distance;
	}
}
