package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.entity.Receiver;
import com.firstlogistics.companyservice.domain.entity.Supplier;
import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.vo.CompanyAddress;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompanyMapperTest {

    private static final UUID ID = UUID.randomUUID();
    private static final UUID HUB_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    @DisplayName("SUPPLIER 도메인 엔티티를 JPA 엔티티로 변환한다")
    void toJpaEntity_supplier() {
        // given
        Company company = Company.reconstitute(
                ID, HUB_ID, USER_ID, "테스트업체",
                new Supplier(), CompanyStatus.ACTIVE,
                CompanyAddress.of("서울 송파구 송파대로 55", "3층"),
                GeoLocation.of(37.514, 127.106)
        );

        // when
        CompanyJpaEntity jpaEntity = CompanyMapper.toJpaEntity(company);

        // then
        assertThat(jpaEntity.getId()).isEqualTo(ID);
        assertThat(jpaEntity.getHubId()).isEqualTo(HUB_ID);
        assertThat(jpaEntity.getUserId()).isEqualTo(USER_ID);
        assertThat(jpaEntity.getName()).isEqualTo("테스트업체");
        assertThat(jpaEntity.getType()).isEqualTo("SUPPLIER");
        assertThat(jpaEntity.getStatus()).isEqualTo(CompanyStatus.ACTIVE);
        assertThat(jpaEntity.getRoadAddress()).isEqualTo("서울 송파구 송파대로 55");
        assertThat(jpaEntity.getDetailAddress()).isEqualTo("3층");
        assertThat(jpaEntity.getLatitude()).isEqualTo(37.514);
        assertThat(jpaEntity.getLongitude()).isEqualTo(127.106);
    }

    @Test
    @DisplayName("RECEIVER 도메인 엔티티를 JPA 엔티티로 변환한다")
    void toJpaEntity_receiver() {
        // given
        Company company = Company.reconstitute(
                ID, HUB_ID, USER_ID, "수령업체",
                new Receiver(), CompanyStatus.ACTIVE,
                CompanyAddress.of("부산 동구 중앙대로 206", "1층"),
                GeoLocation.of(35.106, 129.032)
        );

        // when
        CompanyJpaEntity jpaEntity = CompanyMapper.toJpaEntity(company);

        // then
        assertThat(jpaEntity.getType()).isEqualTo("RECEIVER");
    }

    @Test
    @DisplayName("JPA 엔티티를 SUPPLIER 도메인 엔티티로 변환한다")
    void toDomain_supplier() {
        // given
        CompanyJpaEntity jpaEntity = new CompanyJpaEntity(
                ID, HUB_ID, USER_ID, "테스트업체",
                CompanyStatus.ACTIVE, "SUPPLIER",
                "서울 송파구 송파대로 55", "3층", 37.514, 127.106
        );

        // when
        Company company = CompanyMapper.toDomain(jpaEntity);

        // then
        assertThat(company.getId()).isEqualTo(ID);
        assertThat(company.getType()).isInstanceOf(Supplier.class);
        assertThat(company.canRegisterProduct()).isTrue();
        assertThat(company.canHoldInventory()).isTrue();
    }

    @Test
    @DisplayName("JPA 엔티티를 RECEIVER 도메인 엔티티로 변환한다")
    void toDomain_receiver() {
        // given
        CompanyJpaEntity jpaEntity = new CompanyJpaEntity(
                ID, HUB_ID, USER_ID, "수령업체",
                CompanyStatus.ACTIVE, "RECEIVER",
                "부산 동구 중앙대로 206", "1층", 35.106, 129.032
        );

        // when
        Company company = CompanyMapper.toDomain(jpaEntity);

        // then
        assertThat(company.getType()).isInstanceOf(Receiver.class);
        assertThat(company.canRegisterProduct()).isFalse();
        assertThat(company.canHoldInventory()).isFalse();
    }

    @Test
    @DisplayName("알 수 없는 타입의 JPA 엔티티를 변환하면 예외가 발생한다")
    void toDomain_unknownType_throwsException() {
        // given
        CompanyJpaEntity jpaEntity = new CompanyJpaEntity(
                ID, HUB_ID, USER_ID, "업체",
                CompanyStatus.ACTIVE, "UNKNOWN",
                "서울 송파구 송파대로 55", "3층", 37.514, 127.106
        );

        // when & then
        assertThatThrownBy(() -> CompanyMapper.toDomain(jpaEntity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("알 수 없는 업체 타입");
    }
}