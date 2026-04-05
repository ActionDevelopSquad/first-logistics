package com.firstlogistics.companyservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import com.firstlogistics.companyservice.application.dto.command.ChangeManagerIdCommand;
import com.firstlogistics.companyservice.application.dto.command.CreateCompanyCommand;
import com.firstlogistics.companyservice.application.dto.command.UpdateCompanyCommand;
import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import com.firstlogistics.companyservice.application.port.HubPort;
import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.entity.Supplier;
import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.event.CompanyActivatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyDeactivatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyDeletedEvent;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import com.firstlogistics.companyservice.domain.vo.CompanyAddress;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import common.event.Events;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CompanyCommandServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private HubPort hubPort;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CompanyCommandService companyCommandService;

    private static final UUID FIXED_HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID FIXED_MANAGER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID FIXED_COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @BeforeEach
    void initEvents() {
        new Events().init(applicationEventPublisher);
    }

    @Nested
    @DisplayName("업체 생성 (register)")
    class Register {

        private CreateCompanyCommand validCommand;

        @BeforeEach
        void setUp() {
            validCommand = new CreateCompanyCommand(
                    FIXED_MANAGER_ID,
                    "테스트업체",
                    "SUPPLIER",
                    "서울특별시 송파구 송파대로 55",
                    "3층",
                    37.514,
                    127.106
            );
        }

        @Test
        @DisplayName("저장된 업체 정보를 반환한다")
        void register_success() {
            // given
            given(hubPort.getHubId(any(GeoLocation.class)))
                    .willReturn(FIXED_HUB_ID);
            given(companyRepository.save(any()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            CompanyResult result = companyCommandService.register(validCommand);

            // then
            assertThat(result.hubId()).isEqualTo(FIXED_HUB_ID);
            assertThat(result.managerId()).isEqualTo(FIXED_MANAGER_ID);
            assertThat(result.name()).isEqualTo("테스트업체");
            assertThat(result.type()).isEqualTo("SUPPLIER");
            assertThat(result.status()).isEqualTo(CompanyStatus.ACTIVE.name());
        }

        @Test
        @DisplayName("허브를 찾을 수 없으면 예외가 발생한다")
        void register_invalidHubId_throwsException() {
            // given
            willThrow(new CompanyException(CompanyErrorCode.INVALID_HUB_ID))
                    .given(hubPort).getHubId(any(GeoLocation.class));

            // when & then
            assertThatThrownBy(() -> companyCommandService.register(validCommand))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.INVALID_HUB_ID.getMessage());
        }

        @Test
        @DisplayName("알 수 없는 업체 타입이면 예외가 발생한다")
        void register_invalidCompanyType_throwsException() {
            // given
            given(hubPort.getHubId(any(GeoLocation.class)))
                    .willReturn(FIXED_HUB_ID);

            CreateCompanyCommand invalidTypeCommand = new CreateCompanyCommand(
                    FIXED_MANAGER_ID, "테스트업체", "INVALID_TYPE",
                    "서울특별시 송파구 송파대로 55", "3층", 37.514, 127.106
            );

            // when & then
            assertThatThrownBy(() -> companyCommandService.register(invalidTypeCommand))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.INVALID_COMPANY_TYPE.getMessage());
        }
    }

    @Nested
    @DisplayName("업체 정보 수정 (update)")
    class Update {

        private Company activeCompany;
        private UpdateCompanyCommand validCommand;

        @BeforeEach
        void setUp() {
            activeCompany = Company.reconstitute(
                    FIXED_COMPANY_ID, FIXED_HUB_ID, FIXED_MANAGER_ID, "기존업체",
                    new Supplier(), CompanyStatus.ACTIVE,
                    CompanyAddress.of("서울특별시 송파구 송파대로 55", "3층"),
                    GeoLocation.of(37.514, 127.106)
            );

            validCommand = new UpdateCompanyCommand(
                    FIXED_COMPANY_ID,
                    "수정업체",
                    "RECEIVER",
                    "부산광역시 동구 중앙대로 206",
                    "2층",
                    35.179,
                    129.075
            );
        }

        @Test
        @DisplayName("수정된 업체 정보를 반환한다")
        void update_success() {
            // given
            UUID newHubId = UUID.fromString("00000000-0000-0000-0000-000000000099");
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.of(activeCompany));
            given(hubPort.getHubId(any(GeoLocation.class)))
                    .willReturn(newHubId);
            given(companyRepository.save(any()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            CompanyResult result = companyCommandService.update(validCommand);

            // then
            assertThat(result.name()).isEqualTo("수정업체");
            assertThat(result.type()).isEqualTo("RECEIVER");
            assertThat(result.hubId()).isEqualTo(newHubId);
        }

        @Test
        @DisplayName("존재하지 않는 companyId로 수정하면 예외가 발생한다")
        void update_companyNotFound_throwsException() {
            // given
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyCommandService.update(validCommand))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("비활성화된 업체를 수정하면 예외가 발생한다")
        void update_inactiveCompany_throwsException() {
            // given
            Company inactiveCompany = Company.reconstitute(
                    FIXED_COMPANY_ID, FIXED_HUB_ID, FIXED_MANAGER_ID, "기존업체",
                    new Supplier(), CompanyStatus.INACTIVE,
                    CompanyAddress.of("서울특별시 송파구 송파대로 55", "3층"),
                    GeoLocation.of(37.514, 127.106)
            );
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.of(inactiveCompany));
            given(hubPort.getHubId(any(GeoLocation.class)))
                    .willReturn(FIXED_HUB_ID);

            // when & then
            assertThatThrownBy(() -> companyCommandService.update(validCommand))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_INACTIVE.getMessage());
        }

        @Test
        @DisplayName("허브를 찾을 수 없으면 예외가 발생한다")
        void update_invalidHubId_throwsException() {
            // given
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.of(activeCompany));
            willThrow(new CompanyException(CompanyErrorCode.INVALID_HUB_ID))
                    .given(hubPort).getHubId(any(GeoLocation.class));

            // when & then
            assertThatThrownBy(() -> companyCommandService.update(validCommand))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.INVALID_HUB_ID.getMessage());
        }

        @Test
        @DisplayName("알 수 없는 업체 타입이면 예외가 발생한다")
        void update_invalidCompanyType_throwsException() {
            // given
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.of(activeCompany));
            given(hubPort.getHubId(any(GeoLocation.class)))
                    .willReturn(FIXED_HUB_ID);

            UpdateCompanyCommand invalidTypeCommand = new UpdateCompanyCommand(
                    FIXED_COMPANY_ID, "수정업체", "INVALID_TYPE",
                    "부산광역시 동구 중앙대로 206", "2층", 35.179, 129.075
            );

            // when & then
            assertThatThrownBy(() -> companyCommandService.update(invalidTypeCommand))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.INVALID_COMPANY_TYPE.getMessage());
        }
    }

    @Nested
    @DisplayName("업체 관리자 ID 수정 (changeManagerId)")
    class ChangeManagerId {

        private static final UUID NEW_MANAGER_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

        private Company activeCompany;

        @BeforeEach
        void setUp() {
            activeCompany = Company.reconstitute(
                    FIXED_COMPANY_ID, FIXED_HUB_ID, FIXED_MANAGER_ID, "테스트업체",
                    new Supplier(), CompanyStatus.ACTIVE,
                    CompanyAddress.of("서울특별시 송파구 송파대로 55", "3층"),
                    GeoLocation.of(37.514, 127.106)
            );
        }

        @Test
        @DisplayName("새로운 관리자 ID로 변경하면 성공한다")
        void changeManagerId_success() {
            // given
            given(companyRepository.existsByManagerId(NEW_MANAGER_ID)).willReturn(false);
            given(companyRepository.findById(FIXED_COMPANY_ID)).willReturn(Optional.of(activeCompany));
            given(companyRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            CompanyResult result = companyCommandService.changeManagerId(
                    new ChangeManagerIdCommand(FIXED_COMPANY_ID, NEW_MANAGER_ID));

            // then
            assertThat(result.managerId()).isEqualTo(NEW_MANAGER_ID);
        }

        @Test
        @DisplayName("이미 업체를 관리하는 담당자 ID로 변경하면 예외가 발생한다")
        void changeManagerId_duplicateManagerId_throwsException() {
            // given
            given(companyRepository.existsByManagerId(NEW_MANAGER_ID)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> companyCommandService.changeManagerId(
                    new ChangeManagerIdCommand(FIXED_COMPANY_ID, NEW_MANAGER_ID)))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.DUPLICATE_MANAGER_ID.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 companyId로 변경하면 예외가 발생한다")
        void changeManagerId_companyNotFound_throwsException() {
            // given
            given(companyRepository.existsByManagerId(NEW_MANAGER_ID)).willReturn(false);
            given(companyRepository.findById(FIXED_COMPANY_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyCommandService.changeManagerId(
                    new ChangeManagerIdCommand(FIXED_COMPANY_ID, NEW_MANAGER_ID)))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("비활성화된 업체의 관리자 ID를 변경하면 예외가 발생한다")
        void changeManagerId_inactiveCompany_throwsException() {
            // given
            Company inactiveCompany = Company.reconstitute(
                    FIXED_COMPANY_ID, FIXED_HUB_ID, FIXED_MANAGER_ID, "테스트업체",
                    new Supplier(), CompanyStatus.INACTIVE,
                    CompanyAddress.of("서울특별시 송파구 송파대로 55", "3층"),
                    GeoLocation.of(37.514, 127.106)
            );
            given(companyRepository.existsByManagerId(NEW_MANAGER_ID)).willReturn(false);
            given(companyRepository.findById(FIXED_COMPANY_ID)).willReturn(Optional.of(inactiveCompany));

            // when & then
            assertThatThrownBy(() -> companyCommandService.changeManagerId(
                    new ChangeManagerIdCommand(FIXED_COMPANY_ID, NEW_MANAGER_ID)))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_INACTIVE.getMessage());
        }
    }

    @Nested
    @DisplayName("업체 삭제 (delete)")
    class Delete {

        private static final UUID FIXED_DELETED_BY = UUID.fromString("00000000-0000-0000-0000-000000000010");

        private Company activeCompany;

        @BeforeEach
        void setUp() {
            activeCompany = Company.reconstitute(
                    FIXED_COMPANY_ID, FIXED_HUB_ID, FIXED_MANAGER_ID, "테스트업체",
                    new Supplier(), CompanyStatus.ACTIVE,
                    CompanyAddress.of("서울특별시 송파구 송파대로 55", "3층"),
                    GeoLocation.of(37.514, 127.106)
            );
        }

        @Test
        @DisplayName("존재하는 업체를 삭제하면 성공한다")
        void delete_success() {
            // given
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.of(activeCompany));
            willDoNothing().given(companyRepository).delete(FIXED_COMPANY_ID, FIXED_DELETED_BY);

            // when
            companyCommandService.delete(FIXED_COMPANY_ID, FIXED_DELETED_BY);

            // then
            verify(companyRepository).delete(FIXED_COMPANY_ID, FIXED_DELETED_BY);
            verify(applicationEventPublisher).publishEvent(any(CompanyDeletedEvent.class));
        }

        @Test
        @DisplayName("존재하지 않는 companyId로 삭제하면 예외가 발생한다")
        void delete_companyNotFound_throwsException() {
            // given
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyCommandService.delete(FIXED_COMPANY_ID, FIXED_DELETED_BY))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("업체 비활성화 (deactivate)")
    class Deactivate {

        private Company activeCompany;

        @BeforeEach
        void setUp() {
            activeCompany = Company.reconstitute(
                    FIXED_COMPANY_ID, FIXED_HUB_ID, FIXED_MANAGER_ID, "테스트업체",
                    new Supplier(), CompanyStatus.ACTIVE,
                    CompanyAddress.of("서울특별시 송파구 송파대로 55", "3층"),
                    GeoLocation.of(37.514, 127.106)
            );
        }

        @Test
        @DisplayName("활성화된 업체를 비활성화하면 INACTIVE 상태로 반환한다")
        void deactivate_success() {
            // given
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.of(activeCompany));
            given(companyRepository.save(any()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            CompanyResult result = companyCommandService.deactivate(FIXED_COMPANY_ID);

            // then
            assertThat(result.status()).isEqualTo(CompanyStatus.INACTIVE.name());
            verify(applicationEventPublisher).publishEvent(any(CompanyDeactivatedEvent.class));
        }

        @Test
        @DisplayName("존재하지 않는 companyId로 비활성화하면 예외가 발생한다")
        void deactivate_companyNotFound_throwsException() {
            // given
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyCommandService.deactivate(FIXED_COMPANY_ID))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("이미 비활성화된 업체를 비활성화하면 예외가 발생한다")
        void deactivate_alreadyInactive_throwsException() {
            // given
            Company inactiveCompany = Company.reconstitute(
                    FIXED_COMPANY_ID, FIXED_HUB_ID, FIXED_MANAGER_ID, "테스트업체",
                    new Supplier(), CompanyStatus.INACTIVE,
                    CompanyAddress.of("서울특별시 송파구 송파대로 55", "3층"),
                    GeoLocation.of(37.514, 127.106)
            );
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.of(inactiveCompany));

            // when & then
            assertThatThrownBy(() -> companyCommandService.deactivate(FIXED_COMPANY_ID))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_ALREADY_INACTIVE.getMessage());
        }
    }

    @Nested
    @DisplayName("업체 활성화 (activate)")
    class Activate {

        private Company inactiveCompany;

        @BeforeEach
        void setUp() {
            inactiveCompany = Company.reconstitute(
                    FIXED_COMPANY_ID, FIXED_HUB_ID, FIXED_MANAGER_ID, "테스트업체",
                    new Supplier(), CompanyStatus.INACTIVE,
                    CompanyAddress.of("서울특별시 송파구 송파대로 55", "3층"),
                    GeoLocation.of(37.514, 127.106)
            );
        }

        @Test
        @DisplayName("비활성화된 업체를 활성화하면 ACTIVE 상태로 반환한다")
        void activate_success() {
            // given
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.of(inactiveCompany));
            given(companyRepository.save(any()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            CompanyResult result = companyCommandService.activate(FIXED_COMPANY_ID);

            // then
            assertThat(result.status()).isEqualTo(CompanyStatus.ACTIVE.name());
            verify(applicationEventPublisher).publishEvent(any(CompanyActivatedEvent.class));
        }

        @Test
        @DisplayName("존재하지 않는 companyId로 활성화하면 예외가 발생한다")
        void activate_companyNotFound_throwsException() {
            // given
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyCommandService.activate(FIXED_COMPANY_ID))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("이미 활성화된 업체를 활성화하면 예외가 발생한다")
        void activate_alreadyActive_throwsException() {
            // given
            Company activeCompany = Company.reconstitute(
                    FIXED_COMPANY_ID, FIXED_HUB_ID, FIXED_MANAGER_ID, "테스트업체",
                    new Supplier(), CompanyStatus.ACTIVE,
                    CompanyAddress.of("서울특별시 송파구 송파대로 55", "3층"),
                    GeoLocation.of(37.514, 127.106)
            );
            given(companyRepository.findById(FIXED_COMPANY_ID))
                    .willReturn(Optional.of(activeCompany));

            // when & then
            assertThatThrownBy(() -> companyCommandService.activate(FIXED_COMPANY_ID))
                    .isInstanceOf(CompanyException.class)
                    .hasMessageContaining(CompanyErrorCode.COMPANY_ALREADY_ACTIVE.getMessage());
        }
    }
}
