package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.DeliveryDetailResult;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryDetailResponse(
	UUID deliveryId,
	UUID orderId,
	DeliveryStatus status,
	HubInfo sourceHub,
	HubInfo destinationHub,
	HubInfo currentHub,
	String roadAddress,
	String detailAddress,
	ReceiverInfo receiver,
	CompanyInfo receiverCompany,
	DeliveryManagerInfo companyDeliveryManager,
	List<RouteDetail> routes,
	LocalDateTime createdAt
) {

	public static DeliveryDetailResponse from(DeliveryDetailResult result) {
		return new DeliveryDetailResponse(
			result.deliveryId(),
			result.orderId(),
			result.status(),
			HubInfo.from(result.sourceHub()),
			HubInfo.from(result.destinationHub()),
			HubInfo.from(result.currentHub()),
			result.roadAddress(),
			result.detailAddress(),
			ReceiverInfo.from(result.receiver()),
			CompanyInfo.from(result.receiverCompany()),
			DeliveryManagerInfo.from(result.companyDeliveryManager()),
			result.routes().stream()
				.map(RouteDetail::from)
				.toList(),
			result.createdAt()
		);
	}

	public record HubInfo(UUID hubId, String name, String roadAddress) {
		public static HubInfo from(DeliveryDetailResult.HubInfo hub) {
			if (hub == null) return null;
			return new HubInfo(hub.hubId(), hub.name(), hub.roadAddress());
		}
	}

	public record ReceiverInfo(UUID userId, String name, String phone) {
		public static ReceiverInfo from(DeliveryDetailResult.ReceiverInfo receiver) {
			if (receiver == null) return null;
			return new ReceiverInfo(receiver.userId(), receiver.name(), receiver.phone());
		}
	}

	public record CompanyInfo(UUID companyId, String name, String roadAddress, String detailAddress) {
		public static CompanyInfo from(DeliveryDetailResult.CompanyInfo company) {
			if (company == null) return null;
			return new CompanyInfo(company.companyId(), company.name(), company.roadAddress(), company.detailAddress());
		}
	}

	public record DeliveryManagerInfo(String name, String phone) {
		public static DeliveryManagerInfo from(DeliveryDetailResult.DeliveryManagerInfo manager) {
			if (manager == null) return null;
			return new DeliveryManagerInfo(manager.name(), manager.phone());
		}
	}

	public record RouteDetail(
		UUID routeId,
		int sequence,
		HubInfo sourceHub,
		HubInfo destinationHub,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes,
		int actualDistanceMeters,
		int actualDurationMinutes,
		RouteStatus status,
		DeliveryManagerInfo hubDeliveryManager,
		LocalDateTime expectedStartAt,
		LocalDateTime expectedEndAt
	) {
		public static RouteDetail from(DeliveryDetailResult.RouteDetail route) {
			return new RouteDetail(
				route.routeId(),
				route.sequence(),
				HubInfo.from(route.sourceHub()),
				HubInfo.from(route.destinationHub()),
				route.estimatedDistanceMeters(),
				route.estimatedDurationMinutes(),
				route.actualDistanceMeters(),
				route.actualDurationMinutes(),
				route.status(),
				DeliveryManagerInfo.from(route.hubDeliveryManager()),
				route.expectedStartAt(),
				route.expectedEndAt()
			);
		}
	}
}
