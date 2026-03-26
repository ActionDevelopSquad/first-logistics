package com.firstlogistics.deliverservice.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StaffDetail {

	private String staffName;
	private String phoneNumber;

	public static StaffDetail of(String staffName, String phoneNumber) {
		StaffDetail staffDetail = new StaffDetail();
		staffDetail.staffName = staffName;
		staffDetail.phoneNumber = phoneNumber;
		return staffDetail;
	}
}
