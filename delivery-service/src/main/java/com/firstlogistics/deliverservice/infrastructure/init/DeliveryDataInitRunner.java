package com.firstlogistics.deliverservice.infrastructure.init;

import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * [Feign 조회 전용] 로컬/개발 환경 기동 시 배송담당자 테스트 데이터 삽입.
 * Kafka/Feign 없이 DB에 직접 저장하며, 타 서비스의 Feign 호출 시 데이터 존재를 보장하기 위한 용도.
 * E2E 테스트는 delivery-setup.http 사용.
 *
 * UUID 체계는 모든 서비스 DataInitRunner와 공유합니다.
 */
@Slf4j
//@Component  // TODO: 통합테스트 시 러너 데이터 충돌 방지 - 필요 시 주석 해제
//@Profile("dev")
@RequiredArgsConstructor
public class DeliveryDataInitRunner implements ApplicationRunner {

	private final DeliveryManagerRepository deliveryManagerRepository;

	private static final UUID SEOUL_HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final UUID BUSAN_HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

	private static final TestManagerData[] HUB_DELIVERY_MANAGERS = {
		new TestManagerData("04000000-0000-0000-0000-000000000001", "담당자 허브배송1", "010-4444-0001", SEOUL_HUB_ID, "U_HUBDM01"),
		new TestManagerData("04000000-0000-0000-0000-000000000002", "담당자 허브배송2", "010-4444-0002", SEOUL_HUB_ID, "U_HUBDM02"),
		new TestManagerData("04000000-0000-0000-0000-000000000003", "담당자 허브배송3", "010-4444-0003", SEOUL_HUB_ID, "U_HUBDM03"),
	};

	private static final TestManagerData[] COMPANY_DELIVERY_MANAGERS_SEOUL = {
		new TestManagerData("05000000-0000-0000-0000-000000000001", "담당자 업체배송서울1", "010-5555-0001", SEOUL_HUB_ID, "U_COMPDM_S01"),
		new TestManagerData("05000000-0000-0000-0000-000000000002", "담당자 업체배송서울2", "010-5555-0002", SEOUL_HUB_ID, "U_COMPDM_S02"),
		new TestManagerData("05000000-0000-0000-0000-000000000003", "담당자 업체배송서울3", "010-5555-0003", SEOUL_HUB_ID, "U_COMPDM_S03"),
	};

	private static final TestManagerData[] COMPANY_DELIVERY_MANAGERS_BUSAN = {
		new TestManagerData("05000000-0000-0000-0001-000000000001", "담당자 업체배송부산1", "010-6666-0001", BUSAN_HUB_ID, "U_COMPDM_B01"),
		new TestManagerData("05000000-0000-0000-0001-000000000002", "담당자 업체배송부산2", "010-6666-0002", BUSAN_HUB_ID, "U_COMPDM_B02"),
		new TestManagerData("05000000-0000-0000-0001-000000000003", "담당자 업체배송부산3", "010-6666-0003", BUSAN_HUB_ID, "U_COMPDM_B03"),
	};

	@Override
	public void run(ApplicationArguments args) {
		log.info("===== 배송담당자 테스트 데이터 초기화 시작 =====");

		int created = 0;
		created += initManagers(HUB_DELIVERY_MANAGERS, ManagerType.HUB_DELIVERY);
		created += initManagers(COMPANY_DELIVERY_MANAGERS_SEOUL, ManagerType.COMPANY_DELIVERY);
		created += initManagers(COMPANY_DELIVERY_MANAGERS_BUSAN, ManagerType.COMPANY_DELIVERY);

		log.info("===== 배송담당자 테스트 데이터 초기화 완료: {}명 생성 =====", created);
	}

	private int initManagers(TestManagerData[] managers, ManagerType managerType) {
		int created = 0;
		for (TestManagerData managerData : managers) {
			UUID userId = UUID.fromString(managerData.userId);
			if (deliveryManagerRepository.existsByUserId(userId)) {
				log.debug("이미 존재하는 배송담당자 스킵 - userId: {}", userId);
				continue;
			}

			int nextSequence = deliveryManagerRepository.findNextSequence();
			DeliveryManager deliveryManager = DeliveryManager.create(
				userId, managerData.name, managerData.phone,
				managerData.hubId, managerData.slackId,
				managerType, nextSequence
			);

			deliveryManagerRepository.save(deliveryManager);
			log.info("배송담당자 생성 - name: {}, type: {}, hubId: {}", managerData.name, managerType, managerData.hubId);
			created++;
		}
		return created;
	}

	private record TestManagerData(String userId, String name, String phone, UUID hubId, String slackId) {}
}
