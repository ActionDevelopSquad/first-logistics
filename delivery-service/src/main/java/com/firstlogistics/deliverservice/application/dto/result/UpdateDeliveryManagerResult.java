package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;

import java.util.UUID;

public record UpdateDeliveryManagerResult(
	UUID managerId,
	UUID hubId,
	ManagerType managerType
) {

	public static UpdateDeliveryManagerResult from(DeliveryManager manager) {
		return new UpdateDeliveryManagerResult(
			manager.getId().id(),
			manager.getHubId(),
			manager.getManagerType()
		);
	}
}
