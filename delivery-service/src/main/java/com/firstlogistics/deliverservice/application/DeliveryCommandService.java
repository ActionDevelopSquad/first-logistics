package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryResult;
import com.firstlogistics.deliverservice.application.port.DeliveryEventProducer;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.entity.DeliveryRoute;
import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryStaffRepository;
import com.firstlogistics.deliverservice.infrastructure.feign.CompanyClient;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import com.firstlogistics.deliverservice.infrastructure.feign.UserClient;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.CompanyResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryCommandService {

	private final DeliveryRepository deliveryRepository;
	private final DeliveryStaffRepository deliveryStaffRepository;
	private final CompanyClient companyClient;
	private final HubClient hubClient;
	private final UserClient userClient;
	private final DeliveryEventProducer deliveryEventProducer;

	public DeliveryResult createDelivery(CreateDeliveryCommand command) {
		// 1. 중복 배송 체크
		if (deliveryRepository.existsByOrderId(command.orderId())) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_ALREADY_EXISTS);
		}

		// 2. 수령 업체 조회 (destinationHubId 확보)
		CompanyResponse company = companyClient.getCompany(command.receiverCompanyId()).data();
		UUID destinationHubId = company.hubId();

		// 3. 허브 경로 조회
		HubRouteResponse hubRoute = hubClient.getHubRoute(command.sourceHubId(), destinationHubId).data();

		// 4. 허브 배송담당자 배정 - 경로 스텝마다 한 명씩 (순번 기준)
		// TODO: [동시성] findNextHubStaff → save 사이 레이스 컨디션 존재. 분산락(Redis) 적용 필요 - 락 키: hub:staff:assign:{hubId}
		List<DeliveryStaff> hubStaffs = new ArrayList<>();
		for (HubRouteStepResponse step : hubRoute.routes()) {
			DeliveryStaff hubStaff = deliveryStaffRepository.findNextHubStaff(step.sourceHubId())
				.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.HUB_DELIVERY_STAFF_NOT_AVAILABLE));
			hubStaffs.add(hubStaff);
		}

		// 5. 업체 배송담당자 배정 (순번 기준)
		// TODO: [동시성] findNextCompanyStaff → save 사이 레이스 컨디션 존재. 분산락(Redis) 적용 필요 - 락 키: hub:staff:assign:{hubId}
		DeliveryStaff companyStaff = deliveryStaffRepository.findNextCompanyStaff(destinationHubId)
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.COMPANY_DELIVERY_STAFF_NOT_AVAILABLE));

		// 6. 수령인 조회 (slackId 확보)
		UserResponse receiver = userClient.getUser(command.receiverId()).data();

		// 7. 배송 생성
		Delivery delivery = Delivery.create(
			command.orderId(),
			command.sourceHubId(),
			destinationHubId,
			command.roadAddress(),
			command.detailAddress(),
			command.latitude(),
			command.longitude(),
			command.receiverId(),
			receiver.slackId(),
			command.receiverCompanyId(),
			companyStaff.getId()
		);

		// 8. 경로 일괄 생성 및 허브 배송담당자 배정
		for (int i = 0; i < hubRoute.routes().size(); i++) {
			HubRouteStepResponse step = hubRoute.routes().get(i);
			DeliveryRoute route = DeliveryRoute.create(
				null,
				i,
				step.sourceHubId(),
				step.destinationHubId(),
				step.distanceMeters(),
				step.durationMinutes()
			);
			route.assignStaff(hubStaffs.get(i).getId());
			delivery.assignRoute(route);
		}

		// 9. 저장 및 이벤트 발행
		Delivery savedDelivery = deliveryRepository.save(delivery);
		// TODO: 배송 생성 이벤트 발행 테스트 작성 후 주석 해제
		// deliveryEventProducer.sendCreated(savedDelivery);

		return DeliveryResult.from(savedDelivery);
	}
}
