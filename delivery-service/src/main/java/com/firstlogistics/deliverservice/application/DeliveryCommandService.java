package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.port.DeliveryEventProducer;
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

	public void createDelivery(CreateDeliveryCommand command) {
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
		List<DeliveryStaff> hubStaffs = new ArrayList<>();
		for (HubRouteStepResponse step : hubRoute.routes()) {
			DeliveryStaff hubStaff = deliveryStaffRepository.findNextHubStaff(step.sourceHubId())
				.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.HUB_DELIVERY_STAFF_NOT_AVAILABLE));
			hubStaffs.add(hubStaff);
		}

		// 5. 업체 배송담당자 배정 (순번 기준)
		DeliveryStaff companyStaff = deliveryStaffRepository.findNextCompanyStaff(destinationHubId)
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.COMPANY_DELIVERY_STAFF_NOT_AVAILABLE));

		// 6. 수령인 조회 (slackId 확보)
		UserResponse receiver = userClient.getUser(command.receiverId()).data();

		// TODO: 배송 생성 성공 로직
		// - Delivery.create() 호출
		// - hubRoute.routes()와 hubStaffs를 인덱스로 매핑해 DeliveryRoute 일괄 생성
		// - deliveryRepository.save()
		// - deliveryEventProducer.sendCreated()

		// TODO: [동시성] hubStaff, companyStaff 중복 배정 방지 필요
		//   findNextHubStaff/findNextCompanyStaff → save 사이 레이스 컨디션 존재 (동시 배송 생성 시 동일 담당자 중복 배정 가능)
		//   해결 방안: 분산락 (Redis) - 락 키: hub:staff:assign:{hubId}
	}
}
