package com.firstlogistics.deliverservice.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

	private String sidoCode;
	private String sidoName;
	private String sigunguCode;
	private String sigunguName;
	private String dongCode;
	private String dongName;
	private String roadAddress;
	private String detailAddress;

	public static Address of(
		String sidoCode,
		String sidoName,
		String sigunguCode,
		String sigunguName,
		String dongCode,
		String dongName,
		String roadAddress,
		String detailAddress
	) {
		Address address = new Address();
		address.sidoCode = sidoCode;
		address.sidoName = sidoName;
		address.sigunguCode = sigunguCode;
		address.sigunguName = sigunguName;
		address.dongCode = dongCode;
		address.dongName = dongName;
		address.roadAddress = roadAddress;
		address.detailAddress = detailAddress;
		return address;
	}
}
