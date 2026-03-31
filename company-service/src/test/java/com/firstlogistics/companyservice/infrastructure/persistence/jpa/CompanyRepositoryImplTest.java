package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.entity.Supplier;
import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.vo.CompanyAddress;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyRepositoryImplTest {

    @Mock
    private CompanyJpaRepository companyJpaRepository;

    @InjectMocks
    private CompanyRepositoryImpl companyRepositoryImpl;

    @Test
    @DisplayName("도메인 엔티티를 저장하고 다시 도메인 엔티티로 반환한다")
    void save_success() {
        // given
        UUID id = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Company company = Company.reconstitute(
                id, hubId, userId, "테스트업체",
                new Supplier(), CompanyStatus.ACTIVE,
                CompanyAddress.of("서울 송파구 송파대로 55", "3층"),
                GeoLocation.of(37.514, 127.106)
        );

        CompanyJpaEntity jpaEntity = CompanyMapper.toJpaEntity(company);
        given(companyJpaRepository.save(any())).willReturn(jpaEntity);

        // when
        Company saved = companyRepositoryImpl.save(company);

        // then
        verify(companyJpaRepository).save(any(CompanyJpaEntity.class));
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getName()).isEqualTo("테스트업체");
        assertThat(saved.getHubId()).isEqualTo(hubId);
    }
}