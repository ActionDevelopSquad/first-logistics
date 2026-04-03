package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.entity.Supplier;
import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.vo.CompanyAddress;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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

    private static final UUID COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID MANAGER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private Company company;
    private CompanyJpaEntity jpaEntity;

    @BeforeEach
    void setUp() {
        company = Company.reconstitute(
                COMPANY_ID, HUB_ID, MANAGER_ID, "테스트업체",
                new Supplier(), CompanyStatus.ACTIVE,
                CompanyAddress.of("서울 송파구 송파대로 55", "3층"),
                GeoLocation.of(37.514, 127.106)
        );
        jpaEntity = CompanyMapper.toJpaEntity(company);
    }

    @Test
    @DisplayName("도메인 엔티티를 저장하고 다시 도메인 엔티티로 반환한다")
    void save_success() {
        // given
        given(companyJpaRepository.save(any())).willReturn(jpaEntity);

        // when
        Company saved = companyRepositoryImpl.save(company);

        // then
        verify(companyJpaRepository).save(any(CompanyJpaEntity.class));
        assertThat(saved.getId()).isEqualTo(COMPANY_ID);
        assertThat(saved.getName()).isEqualTo("테스트업체");
        assertThat(saved.getHubId()).isEqualTo(HUB_ID);
    }

    @Nested
    @DisplayName("ID로 단건 조회 (findById)")
    class FindById {

        @Test
        @DisplayName("존재하는 ID로 조회하면 도메인 엔티티를 반환한다")
        void findById_success() {
            // given
            given(companyJpaRepository.findById(COMPANY_ID))
                    .willReturn(Optional.of(jpaEntity));

            // when
            Optional<Company> result = companyRepositoryImpl.findById(COMPANY_ID);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(COMPANY_ID);
            assertThat(result.get().getManagerId()).isEqualTo(MANAGER_ID);
            assertThat(result.get().getName()).isEqualTo("테스트업체");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void findById_notFound_returnsEmpty() {
            // given
            given(companyJpaRepository.findById(COMPANY_ID))
                    .willReturn(Optional.empty());

            // when
            Optional<Company> result = companyRepositoryImpl.findById(COMPANY_ID);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("담당자 ID로 단건 조회 (findByManagerId)")
    class FindByManagerId {

        @Test
        @DisplayName("존재하는 managerId로 조회하면 도메인 엔티티를 반환한다")
        void findByManagerId_success() {
            // given
            given(companyJpaRepository.findByManagerId(MANAGER_ID))
                    .willReturn(Optional.of(jpaEntity));

            // when
            Optional<Company> result = companyRepositoryImpl.findByManagerId(MANAGER_ID);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(COMPANY_ID);
            assertThat(result.get().getManagerId()).isEqualTo(MANAGER_ID);
            assertThat(result.get().getName()).isEqualTo("테스트업체");
        }

        @Test
        @DisplayName("존재하지 않는 managerId로 조회하면 빈 Optional을 반환한다")
        void findByManagerId_notFound_returnsEmpty() {
            // given
            given(companyJpaRepository.findByManagerId(MANAGER_ID))
                    .willReturn(Optional.empty());

            // when
            Optional<Company> result = companyRepositoryImpl.findByManagerId(MANAGER_ID);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("업체 소프트 삭제 (delete)")
    class Delete {

        private static final UUID DELETED_BY = UUID.fromString("00000000-0000-0000-0000-000000000010");

        @Test
        @DisplayName("존재하는 업체를 삭제하면 deletedAt, deletedBy가 설정되고 저장된다")
        void delete_success() {
            // given
            given(companyJpaRepository.findById(COMPANY_ID))
                    .willReturn(Optional.of(jpaEntity));
            given(companyJpaRepository.save(any())).willReturn(jpaEntity);

            // when
            companyRepositoryImpl.delete(COMPANY_ID, DELETED_BY);

            // then
            verify(companyJpaRepository).save(jpaEntity);
            assertThat(jpaEntity.getDeletedAt()).isNotNull();
            assertThat(jpaEntity.getDeletedBy()).isEqualTo(DELETED_BY);
        }
    }

    @Nested
    @DisplayName("담당자 ID 존재 여부 확인 (existsByManagerId)")
    class ExistsByManagerId {

        @Test
        @DisplayName("해당 managerId를 가진 활성 업체가 존재하면 true를 반환한다")
        void existsByManagerId_exists_returnsTrue() {
            // given
            given(companyJpaRepository.existsByManagerId(MANAGER_ID))
                    .willReturn(true);

            // when
            boolean result = companyRepositoryImpl.existsByManagerId(MANAGER_ID);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("해당 managerId를 가진 활성 업체가 없으면 false를 반환한다")
        void existsByManagerId_notExists_returnsFalse() {
            // given
            given(companyJpaRepository.existsByManagerId(MANAGER_ID))
                    .willReturn(false);

            // when
            boolean result = companyRepositoryImpl.existsByManagerId(MANAGER_ID);

            // then
            assertThat(result).isFalse();
        }
    }
}