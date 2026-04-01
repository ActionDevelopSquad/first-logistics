package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record DeliveryDetailResult(
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
	DeliveryStaffInfo companyDeliveryStaff,
	List<RouteDetail> routes
) {

	public static DeliveryDetailResult from(
		DeliveryDetail detail,
		List<DeliveryDetail.RouteDetail> routes,
		Map<UUID, HubResponse> hubMap,
		UserResponse receiver,
		CompanyResponse company
	) {
		return new DeliveryDetailResult(
			detail.deliveryId(),
			detail.orderId(),
			detail.status(),
			HubInfo.from(hubMap.get(detail.sourceHubId())),
			HubInfo.from(hubMap.get(detail.destinationHubId())),
			HubInfo.from(hubMap.get(detail.currentHubId())),
			detail.roadAddress(),
			detail.detailAddress(),
			ReceiverInfo.from(receiver),
			CompanyInfo.from(company),
			DeliveryStaffInfo.from(detail.companyStaffName(), detail.companyStaffPhone()),
			routes.stream()
				.map(route -> RouteDetail.from(route, hubMap))
				.toList()
		);
	}

	public record HubInfo(UUID hubId, String name, String address) {
		public static HubInfo from(HubResponse hub) {
			if (hub == null) return null;
			return new HubInfo(hub.hubId(), hub.name(), hub.address());
		}
	}

	public record ReceiverInfo(UUID userId, String name, String phone) {
		public static ReceiverInfo from(UserResponse user) {
			if (user == null) return null;
			return new ReceiverInfo(user.userId(), user.name(), user.phone());
		}
	}

	public record CompanyInfo(UUID companyId, String name, String address) {
		public static CompanyInfo from(CompanyResponse company) {
			if (company == null) return null;
			return new CompanyInfo(company.companyId(), company.name(), company.address());
		}
	}

	public record DeliveryStaffInfo(String name, String phone) {
		public static DeliveryStaffInfo from(String name, String phone) {
			return new DeliveryStaffInfo(name, phone);
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
		DeliveryStaffInfo hubDeliveryStaff
	) {
		public static RouteDetail from(DeliveryDetail.RouteDetail route, Map<UUID, HubResponse> hubMap) {
			return new RouteDetail(
				route.routeId(),
				route.sequence(),
				HubInfo.from(hubMap.get(route.sourceHubId())),
				HubInfo.from(hubMap.get(route.destinationHubId())),
				route.estimatedDistanceMeters(),
				route.estimatedDurationMinutes(),
				route.actualDistanceMeters(),
				route.actualDurationMinutes(),
				route.status(),
				DeliveryStaffInfo.from(route.staffName(), route.staffPhone())
			);
		}
	}
}
