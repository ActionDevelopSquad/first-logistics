package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.UpdateDeliveryManagerResult;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;

import java.util.UUID;

public record UpdateDeliveryManagerResponse(
	UUID managerId,
	UUID hubId,
	ManagerType managerType
) {

	public static UpdateDeliveryManagerResponse from(UpdateDeliveryManagerResult result) {
		return new UpdateDeliveryManagerResponse(
			result.managerId(),
			result.hubId(),
			result.managerType()
		);
	}
}
