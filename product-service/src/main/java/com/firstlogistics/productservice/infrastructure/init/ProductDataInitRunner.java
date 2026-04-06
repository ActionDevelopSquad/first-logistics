package com.firstlogistics.productservice.infrastructure.init;

import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import com.firstlogistics.productservice.inventory.domain.repository.InventoryRepository;
import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import com.firstlogistics.productservice.product.domain.vo.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * [Feign 조회 전용] 로컬/개발 환경 기동 시 상품 및 재고 테스트 데이터 삽입.
 * 타 서비스의 Feign 호출(getProduct, getStock 등) 시 데이터 존재를 보장하기 위한 용도.
 * E2E 테스트는 delivery-setup.http 사용.
 *
 * UUID 체계는 모든 서비스 DataInitRunner와 공유합니다.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class ProductDataInitRunner implements ApplicationRunner {

	private final ProductRepository productRepository;
	private final InventoryRepository inventoryRepository;

	private static final UUID SEOUL_HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final UUID SUPPLIER_COMPANY_ID = UUID.fromString("06000000-0000-0000-0000-000000000001");

	private static final UUID PRODUCT_ID = UUID.fromString("07000000-0000-0000-0000-000000000001");

	@Override
	public void run(ApplicationArguments args) {
		log.info("===== 상품 테스트 데이터 초기화 시작 =====");

		if (productRepository.findById(PRODUCT_ID).isPresent()) {
			log.debug("이미 존재하는 상품 스킵 - productId: {}", PRODUCT_ID);
			log.info("===== 상품 테스트 데이터 초기화 완료 (스킵) =====");
			return;
		}

		Product product = Product.reconstitute(
			PRODUCT_ID, SUPPLIER_COMPANY_ID, SEOUL_HUB_ID,
			"마른오징어 가공품", Money.krw(15000), ProductStatus.SELLING
		);
		productRepository.save(product);
		log.info("상품 생성 - name: 마른오징어 가공품, price: 15000");

		Inventory inventory = Inventory.create(PRODUCT_ID, 100, 0);
		inventoryRepository.save(inventory);
		log.info("재고 생성 - productId: {}, available: 100", PRODUCT_ID);

		log.info("===== 상품 테스트 데이터 초기화 완료 =====");
	}
}
