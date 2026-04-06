package com.firstlogistics.companyservice.infrastructure.init;

import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.entity.Supplier;
import com.firstlogistics.companyservice.domain.entity.Receiver;
import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import com.firstlogistics.companyservice.domain.vo.CompanyAddress;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * [Feign 조회 전용] 로컬/개발 환경 기동 시 업체 테스트 데이터 삽입.
 * 타 서비스의 Feign 호출(getCompany, getCompanyByManagerId 등) 시 데이터 존재를 보장하기 위한 용도.
 * E2E 테스트는 delivery-setup.http 사용.
 *
 * UUID 체계는 모든 서비스 DataInitRunner와 공유합니다.
 */
@Slf4j
//@Component  // TODO: 통합테스트 시 러너 데이터 충돌 방지 - 필요 시 주석 해제
//@Profile("dev")
@RequiredArgsConstructor
public class CompanyDataInitRunner implements ApplicationRunner {

	private final CompanyRepository companyRepository;

	private static final UUID SEOUL_HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final UUID BUSAN_HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

	private static final UUID SUPPLIER_MANAGER_ID = UUID.fromString("03000000-0000-0000-0000-000000000001");
	private static final UUID RECEIVER_MANAGER_ID = UUID.fromString("03000000-0000-0000-0000-000000000002");

	private static final UUID SUPPLIER_COMPANY_ID = UUID.fromString("06000000-0000-0000-0000-000000000001");
	private static final UUID RECEIVER_COMPANY_ID = UUID.fromString("06000000-0000-0000-0000-000000000002");

	@Override
	public void run(ApplicationArguments args) {
		log.info("===== 업체 테스트 데이터 초기화 시작 =====");

		createCompanyIfAbsent(
			SUPPLIER_COMPANY_ID, SEOUL_HUB_ID, SUPPLIER_MANAGER_ID,
			"건조 식품 가공 업체", new Supplier(),
			"서울특별시 강남구 테헤란로 123", "5층",
			37.4979, 127.0276
		);

		createCompanyIfAbsent(
			RECEIVER_COMPANY_ID, BUSAN_HUB_ID, RECEIVER_MANAGER_ID,
			"수산물 도매 업체", new Receiver(),
			"부산 해운대구 해운대로 456", "1층",
			35.1631, 129.1636
		);

		log.info("===== 업체 테스트 데이터 초기화 완료 =====");
	}

	private void createCompanyIfAbsent(UUID companyId, UUID hubId, UUID managerId,
									   String name, com.firstlogistics.companyservice.domain.entity.CompanyType type,
									   String roadAddress, String detailAddress,
									   double latitude, double longitude) {
		if (companyRepository.findById(companyId).isPresent()) {
			log.debug("이미 존재하는 업체 스킵 - {}", name);
			return;
		}
		Company company = Company.reconstitute(
			companyId, hubId, managerId, name, type,
			CompanyStatus.ACTIVE,
			CompanyAddress.of(roadAddress, detailAddress),
			GeoLocation.of(latitude, longitude)
		);
		companyRepository.save(company);
		log.info("업체 생성 - name: {}, type: {}", name, type.getTypeName());
	}
}
