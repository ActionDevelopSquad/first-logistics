package com.firstlogistics.deliverservice.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Time {

	private int minutes;

	public static Time of(int minutes) {
		Time time = new Time();
		time.minutes = minutes;
		return time;
	}
}
