package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
import com.firstlogistics.deliverservice.domain.projection.DeliveryDetailProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	DeliveryManagerInfo companyDeliveryManager,
	List<RouteDetail> routes,
	LocalDateTime createdAt
) {

	public DeliveryDetailResult {
		Objects.requireNonNull(deliveryId, "deliveryId must not be null");
		Objects.requireNonNull(orderId, "orderId must not be null");
		Objects.requireNonNull(status, "status must not be null");
		Objects.requireNonNull(routes, "routes must not be null");
		Objects.requireNonNull(createdAt, "createdAt must not be null");
	}

	public static DeliveryDetailResult from(
		DeliveryDetailProjection detail,
		List<DeliveryDetailProjection.RouteDetail> routes,
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
			DeliveryManagerInfo.from(detail.companyManagerName(), detail.companyManagerPhone()),
			(routes != null ? routes : List.<DeliveryDetailProjection.RouteDetail>of()).stream()
				.map(route -> RouteDetail.from(route, hubMap))
				.toList(),
			detail.createdAt()
		);
	}

	public record HubInfo(UUID hubId, String name, String roadAddress) {

		public HubInfo {
			Objects.requireNonNull(hubId, "hubId must not be null");
		}

		public static HubInfo from(HubResponse hub) {
			if (hub == null) return null;
			return new HubInfo(hub.hubId(), hub.name(), hub.roadAddress());
		}
	}

	public record ReceiverInfo(UUID userId, String name, String phone) {

		public ReceiverInfo {
			Objects.requireNonNull(name, "name must not be null");
		}

		public static ReceiverInfo from(UserResponse user) {
			if (user == null) return null;
			return new ReceiverInfo(user.userId(), user.name(), user.phone());
		}
	}

	public record CompanyInfo(UUID companyId, String name, String roadAddress, String detailAddress) {

		public CompanyInfo {
			Objects.requireNonNull(companyId, "companyId must not be null");
		}

		public static CompanyInfo from(CompanyResponse company) {
			if (company == null) return null;
			return new CompanyInfo(company.companyId(), company.name(), company.roadAddress(), company.detailAddress());
		}
	}

	public record DeliveryManagerInfo(String name, String phone) {

		public DeliveryManagerInfo {
			if ((name == null) != (phone == null)) {
				throw new IllegalArgumentException("name and phone must both be null or both be non-null");
			}
		}

		public static DeliveryManagerInfo from(String name, String phone) {
			return new DeliveryManagerInfo(name, phone);
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

		public RouteDetail {
			Objects.requireNonNull(routeId, "routeId must not be null");
			Objects.requireNonNull(status, "status must not be null");
		}

		public static RouteDetail from(DeliveryDetailProjection.RouteDetail route, Map<UUID, HubResponse> hubMap) {
			HubResponse destHub = hubMap.get(route.destinationHubId());
			return new RouteDetail(
				route.routeId(),
				route.sequence(),
				HubInfo.from(hubMap.get(route.sourceHubId())),
				destHub != null ? HubInfo.from(destHub) : null,
				route.estimatedDistanceMeters(),
				route.estimatedDurationMinutes(),
				route.actualDistanceMeters(),
				route.actualDurationMinutes(),
				route.status(),
				DeliveryManagerInfo.from(route.managerName(), route.managerPhone()),
				route.expectedStartAt(),
				route.expectedEndAt()
			);
		}
	}
}
