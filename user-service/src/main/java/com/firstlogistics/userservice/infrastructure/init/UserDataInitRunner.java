package com.firstlogistics.userservice.infrastructure.init;

import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import com.firstlogistics.userservice.domain.enums.ManagerType;
import common.security.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * [Feign 조회 전용] 로컬/개발 환경 기동 시 테스트 사용자 데이터 삽입.
 * DB에 직접 저장하며 Keycloak 계정은 생성하지 않으므로 로그인 불가.
 * 타 서비스의 Feign 호출(getUserById 등) 시 데이터 존재를 보장하기 위한 용도.
 * 로그인이 필요한 E2E 테스트는 delivery-setup.http 사용.
 *
 * UUID 체계는 모든 서비스 DataInitRunner와 공유합니다.
 */
@Slf4j
//@Component  // TODO: 통합테스트 시 러너 데이터 충돌 방지 - 필요 시 주석 해제
//@Profile("dev")
@RequiredArgsConstructor
public class UserDataInitRunner implements ApplicationRunner {

	private final UserRepository userRepository;

	private static final UUID SEOUL_HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final UUID BUSAN_HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

	private static final TestUser[] TEST_USERS = {
		new TestUser("01000000-0000-0000-0000-000000000001", "master01", "관리자 마스터", "010-1111-0001", "master01@test.com", "U_MASTER01", UserRole.MASTER, null, null),
		new TestUser("02000000-0000-0000-0000-000000000001", "hubmgr01", "관리자 허브", "010-2222-0001", "hubmgr01@test.com", "U_HUBMGR01", UserRole.HUB_MANAGER, SEOUL_HUB_ID, null),
		new TestUser("03000000-0000-0000-0000-000000000001", "compmgr01", "담당자 업체공급", "010-3333-0001", "compmgr01@test.com", "U_COMPMGR01", UserRole.COMPANY_MANAGER, SEOUL_HUB_ID, null),
		new TestUser("03000000-0000-0000-0000-000000000002", "compmgr02", "담당자 업체수령", "010-3333-0002", "compmgr02@test.com", "U_COMPMGR02", UserRole.COMPANY_MANAGER, BUSAN_HUB_ID, null),
		new TestUser("04000000-0000-0000-0000-000000000001", "hubdm01", "담당자 허브배송1", "010-4444-0001", "hubdm01@test.com", "U_HUBDM01", UserRole.DELIVERY_MANAGER, SEOUL_HUB_ID, ManagerType.HUB_DELIVERY),
		new TestUser("04000000-0000-0000-0000-000000000002", "hubdm02", "담당자 허브배송2", "010-4444-0002", "hubdm02@test.com", "U_HUBDM02", UserRole.DELIVERY_MANAGER, SEOUL_HUB_ID, ManagerType.HUB_DELIVERY),
		new TestUser("04000000-0000-0000-0000-000000000003", "hubdm03", "담당자 허브배송3", "010-4444-0003", "hubdm03@test.com", "U_HUBDM03", UserRole.DELIVERY_MANAGER, SEOUL_HUB_ID, ManagerType.HUB_DELIVERY),
		new TestUser("05000000-0000-0000-0000-000000000001", "compdms01", "담당자 업체배송서울1", "010-5555-0001", "compdms01@test.com", "U_COMPDM_S01", UserRole.DELIVERY_MANAGER, SEOUL_HUB_ID, ManagerType.COMPANY_DELIVERY),
		new TestUser("05000000-0000-0000-0000-000000000002", "compdms02", "담당자 업체배송서울2", "010-5555-0002", "compdms02@test.com", "U_COMPDM_S02", UserRole.DELIVERY_MANAGER, SEOUL_HUB_ID, ManagerType.COMPANY_DELIVERY),
		new TestUser("05000000-0000-0000-0000-000000000003", "compdms03", "담당자 업체배송서울3", "010-5555-0003", "compdms03@test.com", "U_COMPDM_S03", UserRole.DELIVERY_MANAGER, SEOUL_HUB_ID, ManagerType.COMPANY_DELIVERY),
		new TestUser("05000000-0000-0000-0001-000000000001", "compdmb01", "담당자 업체배송부산1", "010-6666-0001", "compdmb01@test.com", "U_COMPDM_B01", UserRole.DELIVERY_MANAGER, BUSAN_HUB_ID, ManagerType.COMPANY_DELIVERY),
		new TestUser("05000000-0000-0000-0001-000000000002", "compdmb02", "담당자 업체배송부산2", "010-6666-0002", "compdmb02@test.com", "U_COMPDM_B02", UserRole.DELIVERY_MANAGER, BUSAN_HUB_ID, ManagerType.COMPANY_DELIVERY),
		new TestUser("05000000-0000-0000-0001-000000000003", "compdmb03", "담당자 업체배송부산3", "010-6666-0003", "compdmb03@test.com", "U_COMPDM_B03", UserRole.DELIVERY_MANAGER, BUSAN_HUB_ID, ManagerType.COMPANY_DELIVERY),
	};

	@Override
	public void run(ApplicationArguments args) {
		log.info("===== 테스트 사용자 데이터 초기화 시작 =====");

		int created = 0;
		for (TestUser testUser : TEST_USERS) {
			UUID userId = UUID.fromString(testUser.userId);
			try {
				userRepository.findByIdNotDeleted(userId);
				log.debug("이미 존재하는 사용자 스킵 - userId: {}", userId);
			} catch (Exception exception) {
				User user = User.create(
					userId, testUser.username, testUser.name, testUser.phone,
					testUser.email, testUser.slackId, testUser.role, testUser.hubId, testUser.managerType
				);
				user.approve();
				userRepository.save(user);
				log.info("사용자 생성 - username: {}, role: {}", testUser.username, testUser.role);
				created++;
			}
		}

		log.info("===== 테스트 사용자 데이터 초기화 완료: {}명 생성 =====", created);
	}

	private record TestUser(String userId, String username, String name, String phone,
							String email, String slackId, UserRole role, UUID hubId, ManagerType managerType) {}
}
