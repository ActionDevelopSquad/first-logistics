package com.firstlogistics.companyservice.application;

import com.firstlogistics.companyservice.application.dto.query.CompanySearchQuery;
import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.entity.Supplier;
import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CompanyQueryServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyQueryService companyQueryService;

    private static final UUID COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID MANAGER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private Company company;

    @BeforeEach
    void setUp() {
        company = Company.reconstitute(
                COMPANY_ID, HUB_ID, MANAGER_ID, "테스트업체",
                new Supplier(), CompanyStatus.ACTIVE,
                CompanyAddress.of("서울 송파구 송파대로 55", "3층"),
                GeoLocation.of(37.514, 127.106)
        );
    }

    @Nested
    @DisplayName("목록 조회 (search)")
    class Search {

        @Test
        @DisplayName("조건에 맞는 업체 목록을 페이지로 반환한다")
        void search_returnsPagedResult() {
            // given
            Page<Company> page = new PageImpl<>(List.of(company), PageRequest.of(0, 10), 1);
            given(companyRepository.findAll(any(), any())).willReturn(page);

            CompanySearchQuery query = new CompanySearchQuery(null, null, null, null);

            // when
            Page<CompanyResult> result = companyQueryService.search(query, PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);

            CompanyResult first = result.getContent().get(0);
            assertThat(first.id()).isEqualTo(COMPANY_ID);
            assertThat(first.hubId()).isEqualTo(HUB_ID);
            assertThat(first.managerId()).isEqualTo(MANAGER_ID);
            assertThat(first.name()).isEqualTo("테스트업체");
            assertThat(first.type()).isEqualTo("SUPPLIER");
            assertThat(first.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("조건에 맞는 업체가 없으면 빈 페이지를 반환한다")
        void search_returnsEmptyPage() {
            // given
            Page<Company> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            given(companyRepository.findAll(any(), any())).willReturn(emptyPage);

            CompanySearchQuery query = new CompanySearchQuery("없는업체", null, null, null);

            // when
            Page<CompanyResult> result = companyQueryService.search(query, PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("ID로 단건 조회 (getById)")
    class GetById {

        @Test
        @DisplayName("존재하는 companyId로 조회하면 업체 정보를 반환한다")
        void getById_success() {
            // given
            given(companyRepository.findById(COMPANY_ID)).willReturn(Optional.of(company));

            // when
            CompanyResult result = companyQueryService.getById(COMPANY_ID);

            // then
            assertThat(result.id()).isEqualTo(COMPANY_ID);
            assertThat(result.hubId()).isEqualTo(HUB_ID);
            assertThat(result.managerId()).isEqualTo(MANAGER_ID);
            assertThat(result.name()).isEqualTo("테스트업체");
            assertThat(result.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("존재하지 않는 companyId로 조회하면 COMPANY_NOT_FOUND 예외가 발생한다")
        void getById_notFound_throwsException() {
            // given
            given(companyRepository.findById(COMPANY_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyQueryService.getById(COMPANY_ID))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("담당자 ID로 단건 조회 (getByManagerId)")
    class GetByManagerId {

        @Test
        @DisplayName("존재하는 managerId로 조회하면 업체 정보를 반환한다")
        void getByManagerId_success() {
            // given
            given(companyRepository.findByManagerId(MANAGER_ID)).willReturn(Optional.of(company));

            // when
            CompanyResult result = companyQueryService.getByManagerId(MANAGER_ID);

            // then
            assertThat(result.id()).isEqualTo(COMPANY_ID);
            assertThat(result.managerId()).isEqualTo(MANAGER_ID);
            assertThat(result.name()).isEqualTo("테스트업체");
        }

        @Test
        @DisplayName("존재하지 않는 managerId로 조회하면 COMPANY_NOT_FOUND 예외가 발생한다")
        void getByManagerId_notFound_throwsException() {
            // given
            given(companyRepository.findByManagerId(MANAGER_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyQueryService.getByManagerId(MANAGER_ID))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_NOT_FOUND.getMessage());
        }
    }
}