package com.firstlogistics.hubservice.infrastructure.init;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.GeoLocation;
import com.firstlogistics.hubservice.hub.domain.vo.HubAddress;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerRepository;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Distance;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Time;
import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * [Feign 조회 전용] 로컬/개발 환경 기동 시 허브, 허브 간 이동정보, 허브 관리자 테스트 데이터 삽입.
 * 타 서비스의 Feign 호출(getHub, getHubManager 등) 시 데이터 존재를 보장하기 위한 용도.
 * E2E 테스트는 delivery-setup.http 사용.
 *
 * UUID 체계는 모든 서비스 DataInitRunner와 공유합니다.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class HubDataInitRunner implements ApplicationRunner {

	private final HubRepository hubRepository;
	private final HubConnectionRepository hubConnectionRepository;
	private final HubManagerRepository hubManagerRepository;

	private static final UUID SEOUL_HUB_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final UUID GYEONGGI_HUB_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");
	private static final UUID BUSAN_HUB_UUID = UUID.fromString("00000000-0000-0000-0000-000000000003");

	private static final UUID HUB_MANAGER_USER_ID = UUID.fromString("02000000-0000-0000-0000-000000000001");

	@Override
	public void run(ApplicationArguments args) {
		log.info("===== 허브 테스트 데이터 초기화 시작 =====");

		initHubs();
		initHubConnections();
		initHubManagers();

		log.info("===== 허브 테스트 데이터 초기화 완료 =====");
	}

	private void initHubs() {
		createHubIfAbsent(SEOUL_HUB_UUID, "서울특별시 센터", "서울특별시 송파구 송파대로 55", 37.5145, 127.1050, HubType.METROPOLITAN);
		createHubIfAbsent(GYEONGGI_HUB_UUID, "경기 북부 센터", "경기도 고양시 덕양구 권율대로 570", 37.6584, 126.8320, HubType.GENERAL);
		createHubIfAbsent(BUSAN_HUB_UUID, "부산광역시 센터", "부산 동구 중앙대로 206", 35.1146, 129.0405, HubType.METROPOLITAN);
	}

	private void createHubIfAbsent(UUID hubUuid, String name, String roadAddress, double lat, double lng, HubType type) {
		HubId hubId = HubId.of(hubUuid);
		if (hubRepository.existsByHubId(hubId)) {
			log.debug("이미 존재하는 허브 스킵 - {}", name);
			return;
		}
		Hub hub = Hub.reconstitute(hubId, name, HubAddress.of(roadAddress), GeoLocation.of(lat, lng), HubStatus.ACTIVE, type);
		hubRepository.save(hub);
		log.info("허브 생성 - name: {}, id: {}", name, hubUuid);
	}

	private void initHubConnections() {
		createConnectionIfAbsent(SEOUL_HUB_UUID, GYEONGGI_HUB_UUID, 60, 35000);
		createConnectionIfAbsent(GYEONGGI_HUB_UUID, BUSAN_HUB_UUID, 300, 400000);
		createConnectionIfAbsent(SEOUL_HUB_UUID, BUSAN_HUB_UUID, 330, 450000);
	}

	private void createConnectionIfAbsent(UUID sourceUuid, UUID destUuid, int minutes, int meters) {
		HubId sourceId = HubId.of(sourceUuid);
		HubId destId = HubId.of(destUuid);
		if (hubConnectionRepository.existsBySourceAndDestination(sourceId, destId)) {
			log.debug("이미 존재하는 이동정보 스킵 - {} → {}", sourceUuid, destUuid);
			return;
		}
		HubConnection connection = HubConnection.create(sourceId, destId, Time.of(minutes), Distance.of(meters));
		hubConnectionRepository.save(connection);
		log.info("이동정보 생성 - {} → {} ({}분, {}m)", sourceUuid, destUuid, minutes, meters);
	}

	private void initHubManagers() {
		UserId userId = UserId.of(HUB_MANAGER_USER_ID);
		HubId hubId = HubId.of(SEOUL_HUB_UUID);
		if (hubManagerRepository.existsByUserIdAndHubId(userId, hubId)) {
			log.debug("이미 존재하는 허브 관리자 스킵");
			return;
		}
		HubManager hubManager = HubManager.create(userId, hubId);
		hubManagerRepository.save(hubManager);
		log.info("허브 관리자 생성 - userId: {}, hubId: {}", HUB_MANAGER_USER_ID, SEOUL_HUB_UUID);
	}
}
